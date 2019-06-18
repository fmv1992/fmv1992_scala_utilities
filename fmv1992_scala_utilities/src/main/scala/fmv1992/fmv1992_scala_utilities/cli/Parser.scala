package fmv1992.fmv1992_scala_utilities.cli

import scala.language.higherKinds
// import scala.language.implicitConversions

import ReferenceTypes._

/** Parse a CLI config file. This file consists of:
  *
  * 1.  Empty lines.
  *
  * 2.  Comment lines: Starting with spaces followed by '#'.
  *
  * 3. A 'name:' followed by nested (space indented) attributes.
  *
  * Mandatory attributes:
  *
  * a. 'help:': the help text.
  * b. 'n:': the number of subsequent arguments to be parsed.
  * c. 'type:': data type of the parsed arguments.
  *
  * Example:
  *
  * ```
  * name: debug
  * n: 0
  * type: int
  * help: Help text.
  *
  *
  * name: verbose
  * n: 0
  * type: int
  * help: Help text.
  * ```
  *
  * This design is influenced by <https://github.com/fpinscala/fpinscala>.
  */
object ReferenceTypes {

  /** A parser is a kind of state action that can fail. */
  type Parser[+A] = ParseState ⇒ Result[A]

  case class ParseError(stack: List[(Location, String)] = List()) {}

  case class ParseState(loc: Location) {}

  case class Location(input: String, offset: Int = 0) {}

  sealed trait Result[+A]
  case class Success[+A](get: A, length: Int) extends Result[A]
  case class Failure(get: ParseError, isCommitted: Boolean)
      extends Result[Nothing]

}

/** Define what is common to all parsers. */
trait Parsers[Parser[+ _]] {
  self ⇒
  def string(s: String): Parser[String]
}

/** Define what is common to CLI config parsers. */
object RefereceParser extends Parsers[Parser] {

  def run[A](p: Parser[A])(s: String): Either[ParseError, A] = {
    ???
  }

}

trait CLIConfig

object CLIConfig {

  case object Newline extends CLIConfig
  case class Comment(get: String) extends CLIConfig
  case class Config(get: Map[String, Map[String, String]]) extends CLIConfig

  def CLIConfigParser(P: Parsers[Parser]): Parser[CLIConfig] = {

    import P.{string ⇒ _, _}

    ???
  }

object CLIConfigParser {}
