package models

import com.wix.accord._
import dsl._

/**
  * @todo Documentation required.
  */
case class Credit(amount: Int) {
	def +(that: Credit): Credit = Credit(this.amount + that.amount)
}

object Credit {
	implicit val creditValidator = validator[Credit] { c =>
		c.amount should be > 0
		c.amount should be < 60
	}
	
	implicit def credit2Int(c: Credit) = c.amount
}
