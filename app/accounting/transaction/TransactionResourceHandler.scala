package accounting.transaction

import javax.inject.Inject

import akka.http.scaladsl.model.DateTime
import play.api.libs.json.{JsValue, Json, Writes}

import scala.concurrent.{ExecutionContext, Future}

case class TransactionResource(id: Long, amount: BigDecimal, date: DateTime, kind: String)

object TransactionResource {

  implicit val writes = new Writes[TransactionResource] {
    def writes(txRes: TransactionResource): JsValue = {
      Json.obj(
        "id" -> txRes.id,
        "amount" -> txRes.amount.doubleValue(),
        "date" -> txRes.date.toIsoDateTimeString(),
        "kind" -> txRes.kind
      )
    }
  }
}

class TransactionResourceHandler @Inject()(txRepository: TransactionRepository)(implicit ec: ExecutionContext) {

  def lookup(id: Long): Future[Option[TransactionResource]] = {
    val txFuture = txRepository.lookup(id)
    txFuture.map { maybeTxData =>
      maybeTxData.map { txData =>
        createTxResource(txData)
      }
    }
  }

  def list: Future[Iterable[TransactionResource]] = {
    txRepository.list.map { txDataList =>
      txDataList.map(txData => createTxResource(txData))
    }
  }

  private def createTxResource(txData: TransactionData): TransactionResource = {
    TransactionResource(txData.id, txData.amount, txData.date, txData.kind match {
      case TransactionKind.Credit => "credit"
      case TransactionKind.Debit => "debit"
    })
  }

}