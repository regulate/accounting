import accounting.balance.{Balance, BalanceImpl, BalanceService, BalanceServiceImpl}
import accounting.transaction.{InMemorySetTransactionRepository, TransactionRepository}
import com.google.inject.{AbstractModule, TypeLiteral}
import util.{AtomicLongCounter, IDGenerator}

class Module extends AbstractModule {

  override def configure(): Unit = {
    bind(new TypeLiteral[IDGenerator[Long]]{}).to(classOf[AtomicLongCounter]).asEagerSingleton()
    bind(classOf[TransactionRepository]).to(classOf[InMemorySetTransactionRepository])
    bind(classOf[Balance]).to(classOf[BalanceImpl])
    bind(classOf[BalanceService]).to(classOf[BalanceServiceImpl])
  }

}
