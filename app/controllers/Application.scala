package controllers

import java.util.concurrent.TimeUnit

import actors.StatsActor
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import models.{AuthService, CombinedData}
import play.api.mvc._
import services.{SunService, WeatherService}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.data.Form
import play.api.data.Forms._

case class UserLoginData(username: String, password: String)

class Application (components: ControllerComponents, sunService: SunService, weatherService: WeatherService,
                   actorSystem: ActorSystem, authService: AuthService)
    extends AbstractController(components) {

  val userDataForm = Form {
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserLoginData.apply)(UserLoginData.unapply)
  }

  def index = Action {
    Ok(views.html.index())
  }

  def login = Action {
    Ok(views.html.login())
  }

  def doLogin = Action { implicit request =>
    userDataForm.bindFromRequest.fold(
      formWithErrors => BadRequest,
      userData => {
        val maybeCookie = authService.login(userData.username, userData.password)
        maybeCookie match {
          case Some(cookie) =>
            Redirect("/").withCookies(cookie)
          case None =>
            Ok(views.html.login())
        }
      }
    )
  }

  def data = Action.async {
    val lat = 43.31283
    val lon = -1.97499

    val sunInfoF = sunService.getSunInfo(lat, lon)
    val temperatureF = weatherService.getTemperature(lat, lon)

    implicit val timeout = Timeout(5, TimeUnit.SECONDS)
    val statsCountF = (actorSystem.actorSelection(StatsActor.path) ? StatsActor.GetStats).mapTo[Int]

    for {
      sunInfo <- sunInfoF
      temperature <- temperatureF
      statsCount <- statsCountF
    } yield {
      Ok(Json.toJson(CombinedData(sunInfo, temperature, statsCount)))
    }
  }
}
