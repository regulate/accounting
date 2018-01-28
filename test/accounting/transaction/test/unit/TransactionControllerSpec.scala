package accounting.transaction.test.unit

import accounting.transaction._
import akka.http.scaladsl.model.DateTime
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json._
import play.api.mvc.{Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class TransactionControllerSpec extends PlaySpec with Results with MockitoSugar {

  val txHandler: TransactionResourceHandler = mock[TransactionResourceHandler]
  val mockTxList = List(
    TransactionResource(1, 1000, DateTime.now, "debit"),
    TransactionResource(2, 10, DateTime.now, "credit")
  )

  "list" should {
    "be valid" when {
      "transactions found" in {
        when(txHandler.list) thenReturn Future.successful(mockTxList)
        val ctl = new TransactionController(stubControllerComponents(), txHandler)
        val result = ctl.list.apply(FakeRequest(GET, "/transactions"))

        status(result) mustBe OK
        contentAsJson(result) mustEqual Json.toJson(mockTxList)
      }
      "transactions not found" in {
        when(txHandler.list) thenReturn Future.successful(Set.empty)
        val ctl = new TransactionController(stubControllerComponents(), txHandler)
        val result = ctl.list.apply(FakeRequest(GET, "/transactions"))

        status(result) mustBe NO_CONTENT
      }
    }
  }

  "lookup" should {
    "be valid" in {
      when(txHandler.lookup(mockTxList.head.id)) thenReturn Future.successful(Option(mockTxList.head))
      val ctl = new TransactionController(stubControllerComponents(), txHandler)
      val result = ctl.lookup(mockTxList.head.id).apply(FakeRequest(GET, s"/transactions/${mockTxList.head.id}"))

      status(result) mustBe OK
      contentAsJson(result) mustEqual Json.toJson(mockTxList.head)
    }
    "transaction not found" in {
      when(txHandler.lookup(any[Long])) thenReturn Future.successful(None)
      val ctl = new TransactionController(stubControllerComponents(), txHandler)
      val result = ctl.lookup(mockTxList.head.id).apply(FakeRequest(GET, s"/transactions/999"))

      status(result) mustBe NOT_FOUND
    }
  }

}
