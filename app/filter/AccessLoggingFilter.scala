package filter

import javax.inject.Inject

import akka.stream.Materializer
import play.api.Logger
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Filter that logs incoming requests information such as uri, remote address, request method and request status
  */
class AccessLoggingFilter @Inject()(implicit val mat: Materializer) extends Filter {

  val accessLogger = Logger("access")

  def apply(next: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] = {
    val result = next(request)

    result.foreach(result => {
      val msg = s"Accessing uri=[${request.uri}] from remote-address=[${request.remoteAddress}]" +
        s" with method=[${request.method}] with status=[${result.header.status}]"
      accessLogger.trace(msg)
    })

    result
  }
}