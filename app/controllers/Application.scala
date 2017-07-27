package controllers

import javax.inject._
import play.api.libs.ws.WSClient
import play.api.mvc._
import services.{SunService, WeatherService}
import play.api.libs.concurrent.Execution.Implicits.defaultContext


class Application @Inject() (components: ControllerComponents, ws: WSClient)
    extends AbstractController(components) {

  val sunService = new SunService(ws)
  val weatherService = new WeatherService(ws)

  def index = Action.async {
    val lat = 43.31283
    val lon = -1.97499

    val sunInfoF = sunService.getSunInfo(lat, lon)
    val temperatureF = weatherService.getTemperature(lat, lon)

    for {
      sunInfo <- sunInfoF
      temperature <- temperatureF
    } yield {
      Ok(views.html.index(sunInfo, temperature))
    }
  }
}
