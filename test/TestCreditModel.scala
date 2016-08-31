package test

import models.{AttributeCredit, CategoryCredit, Credit}
import org.specs2.{ScalaCheck, Specification}
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen}
import com.wix.accord._

/**
  * @todo Documentation needed.
  */

class TestCreditModel extends Specification with ScalaCheck { def is = s2"""
   Given a credit
   When validation is performed
   Then credits between 1 and 60 should be acceptable ${creditsAreValid}
   And credits less than 1 or greater than 60 should not ${creditsAreInvalid}
    
   Given a category and attribute credits
   When validation is performed
   Then we should get validated category credits ${categoryCreditsAreValid}
   And we should get validated attribute credits ${attributeCreditsAreValid}
   And invalid category credits should not pass validation ${categoryCreditsAreInvalid}
   And invalid attribute credits should not pass validation ${attributeCreditsAreInvalid}
   """.stripMargin
	
	/**
	  * Define generators used by the above BDD tests.
	  */
	
	// Set up an implicit Credit generator (for good `Credit` instances) and a credit generator for use with props.
	implicit lazy val arbitraryCreditGenerator: Arbitrary[Credit] = Arbitrary(creditGenerator)
	
	// Set up a bunch of generators for various other types, including Credits, AttributeCredits, and CategoryCredits.
	val creditGenerator = Gen.choose(1, 59).map(Credit(_))
	
	// Also set up a bad credit generator so we can make sure validation works.
	val badCreditGenerator = (Gen.choose(-500, 500) suchThat (n => n < 1 || n > 59)).map(Credit(_))
	
	// Generators for category and attribute credits (takes a creditGenerator or a badCreditGenerator).
	def categoryGenerator(theGenerator: Gen[Credit]) = for {
		c <- theGenerator
	} yield CategoryCredit(c)
	
	def attributeGenerator(theGenerator: Gen[Credit]) = for {
		c1 <- theGenerator
		c2 <- theGenerator
		c3 <- theGenerator
		c4 <- theGenerator
	} yield AttributeCredit(c1, c2, c3, c4)
	
	/**
	  * Define the actual test bodies for above BDD tests.
	  */
	
	def creditsAreValid = prop { (a: Credit) => validate(a).isSuccess must beTrue }
	def creditsAreInvalid = forAll(badCreditGenerator) { (c: Credit) => validate(c).isSuccess must beFalse }
	
	def categoryCreditsAreValid = forAll(categoryGenerator(creditGenerator)) { (c: CategoryCredit) => validate(c).isSuccess must beTrue }
	def attributeCreditsAreValid = forAll(attributeGenerator(creditGenerator)) { (c: AttributeCredit) => validate(c).isSuccess must beTrue }
	
	def categoryCreditsAreInvalid = forAll(categoryGenerator(badCreditGenerator)) { (c: CategoryCredit) => validate(c).isSuccess must beFalse }
	def attributeCreditsAreInvalid = forAll(attributeGenerator(badCreditGenerator)) { (c: AttributeCredit) => validate(c).isSuccess must beFalse }
}
