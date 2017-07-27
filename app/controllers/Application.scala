package controllers

import javax.inject._

import models.SunInfo
import play.api._
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext


class Application @Inject() (components: ControllerComponents, ws: WSClient)
    extends AbstractController(components) {
  def index = Action.async {
    val responseF = ws.url("http://api.sunrise-sunset.org/json?lat=43.31283&lng=-1.97499&formatted=0").get()
    responseF.map { response =>
      val json = response.json
      val sunriseTimeStr = (json \ "results" \ "sunrise").as[String]
      val sunsetTimeStr = (json \ "results" \ "sunset").as[String]
      val sunInfo = SunInfo(sunriseTimeStr, sunsetTimeStr)
      Ok(views.html.index(sunInfo))
    }
  }
}
