import akka.actor.ActorSystem
import akka.stream.Materializer
import play.Logger
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.Future

class StatsFilter(actorSystem: ActorSystem, implicit val mat: Materializer) extends Filter {
  override def apply(next: RequestHeader => Future[Result])(request: RequestHeader): Future[Result] = {
    Logger.info(s"Serving another request: ${request.path}")
    next(request)
  }
}
