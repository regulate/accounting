package accounting.transaction

import javax.inject.Inject

import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

class TransactionController @Inject()(tcc: ControllerComponents,
                                      txResourceHandler: TransactionResourceHandler)
  extends AbstractController(tcc) {

  implicit val ec: ExecutionContext = tcc.executionContext

  def list: Action[AnyContent] = Action.async {
    txResourceHandler.list.map { resources =>
      if (resources.isEmpty)
        NoContent
      else
        Ok(Json.toJson(resources))
    }
  }

  def lookup(id: Long): Action[AnyContent] = Action.async {
    txResourceHandler.lookup(id).map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound
    }
  }

}
