package accounting.balance

import javax.inject.Inject

import accounting.balance.FormData.{CreditData, DebitData}
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class BalanceController @Inject()(tcc: ControllerComponents,
                                  balanceService: BalanceService)
  extends AbstractController(tcc) with play.api.i18n.I18nSupport {

  val log = Logger("accounting")
  implicit val ec: ExecutionContext = tcc.executionContext

  def get: Action[AnyContent] = Action.async {
    balanceService.currentBalance.map(balance =>
      Ok(Json.obj("balance" -> balance)))
  }

  def debit: Action[AnyContent] = Action.async { implicit request =>
    processForm(FormData.debitForm, (input: DebitData) =>
      balanceService.receiveDebit(Debit(input.amount))
    )
  }

  def credit: Action[AnyContent] = Action.async { implicit request =>
    processForm(FormData.creditForm, (input: CreditData) =>
      balanceService.receiveCredit(Credit(input.amount))
    )
  }

  private def processForm[A, T](form: Form[T], onSuccess: T => Future[Long])(implicit request: Request[A]): Future[Result] = {
    def failure(badForm: Form[T]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: T) = {
      onSuccess(input).map(txId =>
        Ok(Json.obj("transactionId" -> txId))
      ).recover({ case b: BalanceException =>
        log.info(b.getMessage)
        BadRequest(Json.obj("error" -> b.getMessage))
      })
    }

    form.bindFromRequest().fold(failure, success)
  }

}
