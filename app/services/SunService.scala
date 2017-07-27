package services

import java.time.{ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.SunInfo
import play.api.libs.ws.WSClient

class SunService(wsClient: WSClient) {
  def getSunInfo(lat: Double, lon: Double) = {
    val sunResponseF = wsClient.url("http://api.sunrise-sunset.org/json?lat=43.31283&lng=-1.97499&formatted=0").get()
    sunResponseF.map { sunResponse => sunResponseF
      val json = sunResponse.json
      val sunriseTimeStr = (json \ "results" \ "sunrise").as[String]
      val sunsetTimeStr = (json \ "results" \ "sunset").as[String]
      val sunriseTime = ZonedDateTime.parse(sunriseTimeStr)
      val sunsetTime = ZonedDateTime.parse(sunsetTimeStr)
      val formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.of("Europe/Warsaw"))
      val sunInfo = SunInfo(sunriseTime.format(formatter), sunsetTime.format(formatter))
      sunInfo
    }
  }
}
