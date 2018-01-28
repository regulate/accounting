package accounting.balance

import javax.inject.{Inject, Singleton}

import accounting.transaction.{TransactionData, TransactionKind, TransactionRepository}
import akka.http.scaladsl.model.DateTime
import play.api.Logger
import util.IDGenerator

import scala.concurrent.{ExecutionContext, Future}

trait Transaction {
  def amount: BigDecimal
  def date: DateTime
}

case class Debit(override val amount: BigDecimal,
                 override val date: DateTime = DateTime.now) extends Transaction

case class Credit(override val amount: BigDecimal,
                  override val date: DateTime = DateTime.now) extends Transaction

trait BalanceService {
  def receiveCredit(credit: Credit): Future[Long]
  def receiveDebit(debit: Debit): Future[Long]
  def currentBalance: Future[BigDecimal]
}

@Singleton
class BalanceServiceImpl @Inject()(balance: Balance,
                                   transactionRepository: TransactionRepository,
                                   iDGenerator: IDGenerator[Long])(implicit ec: ExecutionContext) extends BalanceService {

  val log = Logger("accounting")

  override def receiveCredit(credit: Credit): Future[Long] = {
    val withdrawFuture = Future {
      balance.withdraw(credit.amount)
      log.info(s"Received credit for ${credit.amount}")
    }
    withdrawFuture.flatMap(_ => transactionRepository.create(createTxData(credit)))
  }

  override def receiveDebit(debit: Debit): Future[Long] = {
    val depositFuture = Future {
      balance.deposit(debit.amount)
      log.info(s"Received debit for ${debit.amount}")
    }
    depositFuture.flatMap(_ => transactionRepository.create(createTxData(debit)))
  }

  override def currentBalance: Future[BigDecimal] = Future {balance.get}

  private def createTxData(transaction: Transaction): TransactionData = {
    TransactionData(iDGenerator.generate, transaction.amount, transaction.date, transaction match {
      case _: Debit => TransactionKind.Debit
      case _: Credit => TransactionKind.Credit
    })
  }
}
