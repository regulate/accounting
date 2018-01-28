package accounting.transaction

import java.util.concurrent.atomic.AtomicReference
import javax.inject.{Inject, Singleton}

import akka.http.scaladsl.model.DateTime

import scala.collection.immutable.TreeSet
import scala.concurrent.{ExecutionContext, Future}
import scala.math.Ordering.by

case class TransactionData(id: Long, amount: BigDecimal, date: DateTime = DateTime.now, kind: TransactionKind.Value)

object TransactionKind extends Enumeration {
  val Credit, Debit = Value
}

trait TransactionRepository {
  def create(transaction: TransactionData): Future[Long]
  def list: Future[Iterable[TransactionData]]
  def lookup(id: Long): Future[Option[TransactionData]]
}

@Singleton
class InMemorySetTransactionRepository @Inject()()(implicit ec: ExecutionContext) extends TransactionRepository {

  implicit val ordering: Ordering[TransactionData] = by{_.id}
  private val transactions = new AtomicReference[Set[TransactionData]](new TreeSet[TransactionData]())

  override def create(transaction: TransactionData): Future[Long] = {
    Future {
      transactions.set(transactions.get() + transaction)
      transaction.id
    }
  }

  override def list: Future[Iterable[TransactionData]] = {
    Future {
      transactions.get()
    }
  }

  override def lookup(id: Long): Future[Option[TransactionData]] = {
    Future {
      transactions.get().find(t => t.id == id)
    }
  }
}


