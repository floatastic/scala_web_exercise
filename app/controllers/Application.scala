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
    val sunResponseF = ws.url("http://api.sunrise-sunset.org/json?lat=43.31283&lng=-1.97499&formatted=0").get()
    val weatherResponseF = ws.url("http://api.openweathermap.org/data/2.5/weather?lat=43.31283&lon=-1.97499&units=metric&APPID=10b6ee80b1c315a0ccb7f8cf07ab57f2").get()

    for {
      sunResponse <- sunResponseF
      weatherResponse <- weatherResponseF
    } yield {

      val json = sunResponse.json
      val sunriseTimeStr = (json \ "results" \ "sunrise").as[String]
      val sunsetTimeStr = (json \ "results" \ "sunset").as[String]
      val sunriseTime = ZonedDateTime.parse(sunriseTimeStr)
      val sunsetTime = ZonedDateTime.parse(sunsetTimeStr)
      val formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.of("Europe/Warsaw"))
      val sunInfo = SunInfo(sunriseTime.format(formatter), sunsetTime.format(formatter))

      val weatherJson = weatherResponse.json
      val temperature = (weatherJson \ "main" \ "temp").as[Double]

      Ok(views.html.index(sunInfo, temperature))
    }
  }
}
