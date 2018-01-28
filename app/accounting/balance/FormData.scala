package accounting.balance

import play.api.data.Form

object FormData {

  val debitForm: Form[DebitData] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "amount" -> bigDecimal.verifying("error.gtZero", a => a > 0)
      )(DebitData.apply)(DebitData.unapply)
    )
  }

  val creditForm: Form[CreditData] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "amount" -> bigDecimal.verifying("error.gtZero", a => a > 0)
      )(CreditData.apply)(CreditData.unapply)
    )
  }

  case class DebitData(amount: BigDecimal)

  case class CreditData(amount: BigDecimal)

}
