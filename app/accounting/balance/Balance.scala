package accounting.balance

import java.util.concurrent.atomic.AtomicReference
import javax.inject.Singleton

trait Balance {
  def deposit(amount: BigDecimal): Unit
  def withdraw(amount: BigDecimal): Unit
  def get: BigDecimal
}

@Singleton
class BalanceImpl extends Balance {

  val balance: AtomicReference[BigDecimal] = new AtomicReference[BigDecimal](0)

  @throws(classOf[NonPositiveOperationalAmountException])
  override def deposit(amount: BigDecimal): Unit = {
    checkOperAmountPositive(amount)
    balance.set(balance.get() + amount)
  }

  @throws(classOf[InsufficientFundsException])
  @throws(classOf[NonPositiveOperationalAmountException])
  override def withdraw(amount: BigDecimal): Unit = {
    checkOperAmountPositive(amount)
    checkBalance(amount)
    balance.set(balance.get() - amount)
  }

  override def get: BigDecimal = {
    balance.get()
  }

  private def checkOperAmountPositive(amount: BigDecimal): Unit = {
    if (amount.equals(0) || amount < 0) {
      throw NonPositiveOperationalAmountException(s"Operational amount must be positive, but having $amount")
    }
  }

  private def checkBalance(amount: BigDecimal): Unit = {
    if (balance.get() - amount < 0) {
      throw InsufficientFundsException(s"You don't have such amount to withdraw: $amount")
    }
  }

}

abstract class BalanceException(private val message: String,
                                private val cause: Throwable = None.orNull) extends RuntimeException(message, cause)

case class NonPositiveOperationalAmountException(message: String) extends BalanceException(message)

case class InsufficientFundsException(message: String) extends BalanceException(message)
