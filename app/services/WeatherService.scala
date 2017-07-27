package services

import play.api.libs.ws.WSClient
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class WeatherService(wsClient: WSClient) {
  def getTemperature(lat: Double, lon: Double) = {
    val weatherResponseF = wsClient.url("http://api.openweathermap.org/data/2.5/weather?lat=43.31283&lon=-1.97499&units=metric&APPID=10b6ee80b1c315a0ccb7f8cf07ab57f2").get()

    weatherResponseF.map { weatherResponse =>
      val weatherJson = weatherResponse.json
      val temperature = (weatherJson \ "main" \ "temp").as[Double]
      temperature
    }
  }
}
