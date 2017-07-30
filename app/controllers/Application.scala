package controllers

import java.util.concurrent.TimeUnit

import actors.StatsActor
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import models.CombinedData
import play.api.mvc._
import services.{SunService, WeatherService}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json


class Application (components: ControllerComponents, sunService: SunService, weatherService: WeatherService, actorSystem: ActorSystem)
    extends AbstractController(components) {

  def index = Action {
    Ok(views.html.index())
  }

  def login = Action {
    Ok(views.html.login())
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
