package test

import org.joda.time.DateTime
import org.scalacheck.Gen
import org.specs2.Specification
import scala.io.Source
import scala.util.Random
import utility.TestLogging

/**
  * Provides a handful of utility functions that are helpful in the unit testing framework. Intended to be useful when writing
  * individual unit and functional tests.
  *
  * @author zbeckman on 1/17/16.
  *         Taken freely from my personal GitHub repository. These generators are in the public domain and may be used and
  *         shared without restriction.
  */
trait Generators extends Specification with TestLogging {
	/**
	  * Timecode reference to a moment in time that recently passed.
	  */
	val now = new DateTime()
	
	/**
	  * A time index that is helpful in identifying large data sets. Appears as a string such as 139.240, representing the
	  * day of the year followed by the minute of the day. This can be used as a tag, for example, at the beginning of a test
	  * string, thereby helping to identify the data set based on when it was generated.
	  */
	val timeIndex = s"${now.dayOfYear().get()}.${now.minuteOfDay().get()}"
	
	/**
	  * A set of characters usable in generating random phone numbers.
	  */
	val phoneNumberSet: Seq[Char] = List('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', ',', '-')
	
	/**
	  * A set of characters usable in phone numbers punctuation or separators.
	  */
	val phoneNumberSeparators: Seq[Char] = List(' ', ',', '-', '+', '#')
	
	/**
	  * A ScalaCheck generator that creates random phone number digit sequences.
	  */
	val phoneNumberCharacter: Gen[Char] = Gen.oneOf(phoneNumberSet)
	
	/**
	  * A very limited set of Unicode characters, good when generating "pronouncible looking" strings. Not a particularly good
	  * data set for testing though (consider using the unicodeCharacter type instead).
	  */
	val smallUnicodeSet = ('A' to 'Z') ++ ('a' to 'z') ++ ('À' to 'ÿ')
	
	/**
	  * A ScalaCheck generator that creates pronouncible-looking character sequences.
	  */
	val pronouncibleLatinCharacter: Gen[Char] = Gen.oneOf(smallUnicodeSet)
	
	/**
	  * A ScalaCheck generator that creates character sequences from a wide range of Latin characters.
	  */
	val latinUnicodeCharacter = Gen.choose('\u0041', '\u01B5').filter(Character.isDefined)
	
	/**
	  * A ScalaCheck generator that creates character sequences from the entire range of Unicode characters. This includes
	  * Asian languages and symbols.
	  */
	val unicodeCharacter = Gen.choose(Char.MinValue, Char.MaxValue).filter(Character.isDefined)
	
	// MARK: Reusable generators that will output parameterized test values:
	
	/**
	  * A Unicode string generator useful in testing. Can be used to generate random sequences of characters of an arbitrary
	  * length.
	  *
	  * @param generator The character generator to use, an instance of Gen[Char]. Defaults to unicodeCharacter.
	  * @param minimum   An Int that specifies the shortest string to generate.
	  * @param maximum   An Int that specifies the longest string to generate.
	  * @return A randomly generated string.
	  */
	def unicodeGenerator(generator: Gen[Char] = Gen.alphaChar, minimum: Int = 5, maximum: Int = 20): Gen[String] = Gen.chooseNum(minimum, maximum).flatMap { n =>
		Gen.buildableOfN[String, Char](n, generator)
	}
	
	/**
	  * A Unicode string generator useful in testing. Can be used to generate random sequences of characters of an arbitrary
	  * length. The string will have any white space or horizontal space, including horizontal control characters, stripped from
	  * it. This can be helpful when generating data sets for fields that disallow white space.
	  *
	  * Note that this implementation strips horizontal white space from the string after the string is generated. For this reason
	  * the minimum length is a guideline, and the actual returned string may be significantly shorter than the specified minimum
	  * length.
	  *
	  * @param generator The character generator to use, an instance of Gen[Char]. Defaults to unicodeCharacter.
	  * @param minimum   An Int that specifies the shortest string to generate.
	  * @param maximum   An Int that specifies the longest string to generate.
	  * @return A randomly generated string.
	  */
	def unicodeGeneratorNoWhitespace(generator: Gen[Char] = Gen.alphaChar, minimum: Int = 5, maximum: Int = 20): Gen[String] = Gen.chooseNum(minimum, maximum).flatMap { n =>
		Gen.buildableOfN[String, Char](n, generator).map(_.replaceAll("[\\p{Z}\\p{C}]", ""))
	}
	
	/**
	  * Generates a random list of words (using the words.txt file as a source for words, selecting them at random).
	  *
	  * @param minimum The fewest number of words to return.
	  * @param maximum The most number of words to return.
	  * @return A `Gen[List[String]]` containing between `minimum` and `maximum` words.
	  */
	def sentenceGenerator(minimum: Int = 1, maximum: Int = 10): Gen[List[String]] = Gen.chooseNum(minimum, maximum).flatMap { n =>
		Gen.containerOfN[List, String](n, wordGenerator)
	}
	
	lazy val wordSource = Source.fromURL(getClass.getResource("/words.txt")).getLines().toList
	
	/**
	  * A word generator that returns a single, random word taken from the "resources/words.txt" file.
	  *
	  * @return A `String` representing a single word.
	  */
	def wordGenerator = Gen.chooseNum(0, wordSource.length - 1).map(wordSource(_))
	
	//	def wordGenerator(minimum: Int, maximum: Int): Gen[List[String]] = {
	//		Gen.chooseNum(minimum, maximum).flatMap { n =>
	//			for {
	//				c <- 1 to n
	//				w <- wordSource(c)
	//			} yield(w)
	//		}
	//	}
	//

	/**
	  * Generates a short code consisting of alpha-numeric characters. The returned String will be between 3 and 10 characters
	  * long (inclusive).
	  */
	val shortCodeGenerator: Gen[String] = Gen.chooseNum(3, 10).flatMap { n =>
		Gen.sequence[String, Char](List.fill(n)(Gen.alphaNumChar))
	}
	
	/**
	  * Generates random phone numbers of varying length.
	  */
	val phoneNumberGenerator: Gen[String] = Gen.chooseNum(6, 18).flatMap { n =>
		Gen.sequence[String, Char](List.fill(n)(phoneNumberCharacter))
	}
	
	/**
	  * Generators one phone number separator symbol.
	  */
	val phoneNumberSeparatorGenerator: Gen[Char] = Gen.oneOf(phoneNumberSeparators)
	
	/**
	  * Generates random email addresses using the specified generator. If not specified, the unicodeGeneratorNoWhitespace will
	  * be used. Optionally, a time index can be prepended to the email address to aid in identification of test values.
	  *
	  * @param generator A generator to use. Defaults to unicodeGeneratorNoWhitespace.
	  * @param withIndex Whether or not to include a time index at the beginning of the email address. Defaults to false.
	  * @return A String containing a randomly generated email address.
	  */
	def emailGenerator(generator: Gen[String] = unicodeGeneratorNoWhitespace(), withIndex: Boolean = false): Gen[String] = generator.map(s => ((if (withIndex) timeIndex else "") + s + "@" + simpleRandomShortCode() + ".accenture.com"))
	
	/**
	  * Generates random Strings that could be used for names. If not specified, the unicodeGenerator will be used. Optionally,
	  * a time index can be prepended to the name to aid in identification of test values. If more pronouncible names are
	  * desired, unicodeGenerator(pronouncibleLatinCharacter) is a good option. although this dramatically reduces the quality
	  * of compliance testing.
	  *
	  * @param generator A generator to use. Defaults to unicodeGenerator.
	  * @param withIndex Whether or not to include a time index at the beginning of the name. Defaults to false.
	  * @return A String containing a randomly generated name.
	  */
	def nameGenerator(generator: Gen[String] = unicodeGenerator(), withIndex: Boolean = false): Gen[String] = generator.map(s => ((if (withIndex) timeIndex else "") + s.trim.capitalize))
	
	// MARK: Basic random value patterns:
	
	/**
	  * Given an email address in a `String`, randomly mutates the case of the email address, returning a `List` with the
	  * mutations. At least one all-uppercase and one all-lowercase mutation will be included.
	  *
	  * @param email A `String` representing an email address to use as a base pattern.
	  * @param size  The number of mutations to return in the `List`.
	  * @return A `List[String]` containing the specified number of email address (at least two will always be returned).
	  */
	def randomFormattedEmailList(email: String, size: Int): List[String] = {
		email.toUpperCase +: email.toLowerCase +: List.fill(size - 2) {
			email.toVector.map(i => if (Random.nextInt(2) % 2 == 0) i.toUpper else i.toLower).mkString
		}
	}
	
	/**
	  * Generates a simple, randomized short code of up to seven digits (unless overridden with a different size) consisting
	  * of alphanumeric characters.
	  *
	  * @return A String containing the shortcode.
	  */
	def simpleRandomShortCode(length: Int = 7): String = {
		Random.alphanumeric.take(Random.nextInt(length) + 1).mkString
	}
	
	/**
	  * Generates a simple random email with a minimum length of length:. The email will be significantly longer than the
	  * specified length.
	  *
	  * @param length The base length of the first part of the email. The overall email will be longer.
	  * @return A String containing a randomly generated email.
	  */
	def simpleRandomEmail(length: Int = 18): String = {
		Random.alphanumeric.take(length).mkString + "@" + simpleRandomShortCode() + ".accenture.com"
	}
	
	/**
	  * Generates a simple name using basic alphabetic (Latin) characters. The name length will be up to the provided length
	  * (and will be at least two characters). It will be in proper case.
	  *
	  * @param length The maximum length of the generated name.
	  * @return A String containing a randomized name-like string.
	  */
	def simpleRandomName(length: Int = 12): String = {
		Random.alphanumeric.filter(_.isLetter).take(Random.nextInt(length) + 2).mkString.toLowerCase.capitalize
	}
	
	/**
	  * Generates a simple random password using alphanumeric characters, and appends a single symbol to the end of the
	  * password. These will be adequate to pass the password requirements.
	  *
	  * @param length Minimum length of the password.
	  * @return A String consisting of alphanumeric characters plus at least one symbol.
	  */
	def simpleRandomPassword(length: Int = 20): String = {
		Random.alphanumeric.take(length).mkString + "a0#"
	}
	
	/**
	  * Generates a simple alphanumeric string.
	  *
	  * @return Returns an arbitrary length String containing alphanumeric characters (usually of at least 6 characters in
	  *         length).
	  */
	def simpleRandomNumericalString(): String = {
		return ((100000.0 + Random.nextDouble * (10000000000.0 - 100000.0)).toLong).toString
	}
	
	/**
	  * Generates a simple random phone number, consisting of two numbers separated by a hyphen, limited to 20 characters
	  * maximum length.
	  *
	  * @return A simple, randomly generated phone number `String` of up to 20 characters in length.
	  */
	def simpleRandomPhoneNumber(): String = {
		val symbol = Array(" ", "+", "-")
		(simpleRandomNumericalString().take(3) + symbol(Random.nextInt(3)) + simpleRandomNumericalString() + "-" + simpleRandomNumericalString()).take(20)
	}
}
