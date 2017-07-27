package controllers

import javax.inject._

import models.SunInfo
import play.api._
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import java.time.{ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter


class Application @Inject() (components: ControllerComponents, ws: WSClient)
    extends AbstractController(components) {
  def index = Action.async {
    val responseF = ws.url("http://api.sunrise-sunset.org/json?lat=43.31283&lng=-1.97499&formatted=0").get()
    responseF.map { response =>
      val json = response.json
      val sunriseTimeStr = (json \ "results" \ "sunrise").as[String]
      val sunsetTimeStr = (json \ "results" \ "sunset").as[String]
      val sunriseTime = ZonedDateTime.parse(sunriseTimeStr)
      val sunsetTime = ZonedDateTime.parse(sunsetTimeStr)
      val formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.of("Europe/Warsaw"))
      val sunInfo = SunInfo(sunriseTime.format(formatter), sunsetTime.format(formatter))
      Ok(views.html.index(sunInfo))
    }
  }
}
