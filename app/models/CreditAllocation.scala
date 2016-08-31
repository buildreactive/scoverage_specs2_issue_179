package models

import com.wix.accord.dsl._

/**
  * @todo Documentation required.
  */

trait CreditAllocation {
	def totalCredits: Int
}

case class CategoryCredit(room: Credit) extends CreditAllocation {
	def totalCredits: Int = room
}

object CategoryCredit {
	implicit val categoryCreditValidator = validator[CategoryCredit] { c =>
		c.room is valid
	}
}

case class AttributeCredit(bathroom: Credit, living: Credit, beds: Credit, balcony: Credit ) extends CreditAllocation {
	override def totalCredits: Int = bathroom + living + beds + balcony
}

object AttributeCredit {
	implicit val attributeCreditValidator = validator[AttributeCredit] { c =>
		c.bathroom is valid
		c.living is valid
		c.beds is valid
		c.balcony is valid
	}
}
