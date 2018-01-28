package accounting.balance.test.unit

import accounting.balance._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.mvc.{Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class BalanceControllerSpec extends PlaySpec with Results with MockitoSugar {

  val service: BalanceService = mock[BalanceService]

  "get" should {
    "be valid" in {
      when(service.currentBalance) thenReturn Future.successful(BigDecimal.int2bigDecimal(100))
      val ctl = new BalanceController(stubControllerComponents(), service)
      val result: Future[Result] = ctl.get.apply(FakeRequest())
      val json = contentAsJson(result)

      status(result) mustBe OK
      (json \ "balance").as[BigDecimal] mustBe BigDecimal.int2bigDecimal(100)
    }
  }

  "credit" should {
    "be valid" in {
      val credit = Credit(100)
      when(service.receiveCredit(credit)) thenReturn Future.successful(1L)
      val ctl = new BalanceController(stubControllerComponents(), service)
      val req = FakeRequest(POST, "/balance/credit").withJsonBody(Json.obj("amount" -> credit.amount))
      val result: Future[Result] = ctl.credit.apply(req)
      val json = contentAsJson(result)

      status(result) mustBe OK
      (json \ "transactionId").as[Long] mustBe 1L
    }
    "throw InsufficientFundsException if insufficient balance" in {
      val credit = Credit(100)
      when(service.receiveCredit(credit)) thenThrow InsufficientFundsException("not enough funds")
      val ctl = new BalanceController(stubControllerComponents(), service)
      val req = FakeRequest(POST, "/balance/credit").withJsonBody(Json.obj("amount" -> credit.amount))
      a[InsufficientFundsException] must be thrownBy {
        val result: Future[Result] = ctl.credit.apply(req)
        val json = contentAsJson(result)
        status(result) mustBe BadRequest
        (json \ "error").as[String] mustBe "not enough funds"
      }
    }
  }

  "debit" should {
    "be valid" in {
      val debit = Debit(100)
      when(service.receiveDebit(debit)) thenReturn Future.successful(1L)
      val ctl = new BalanceController(stubControllerComponents(), service)
      val req = FakeRequest(POST, "/balance/debit").withJsonBody(Json.obj("amount" -> debit.amount))
      val result: Future[Result] = ctl.debit.apply(req)
      val json = contentAsJson(result)

      status(result) mustBe OK
      (json \ "transactionId").as[Long] mustBe 1L
    }
  }
}
