package controllers

import java.util.concurrent.TimeUnit

import actors.StatsActor
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import play.api.mvc._
import services.{SunService, WeatherService}
import play.api.libs.concurrent.Execution.Implicits.defaultContext


class Application (components: ControllerComponents, sunService: SunService, weatherService: WeatherService, actorSystem: ActorSystem)
    extends AbstractController(components) {

  def index = Action.async {
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
      Ok(views.html.index(sunInfo, temperature, statsCount))
    }
  }
}
