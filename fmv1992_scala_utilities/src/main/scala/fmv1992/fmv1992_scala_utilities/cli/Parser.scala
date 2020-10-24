package fmv1992.fmv1992_scala_utilities.cli

import ParserTypes._

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
  * name: verbose
  * n: 0
  * type: int
  * help: Help text.
  * ```
  *
  * This design is influenced by <https://github.com/fpinscala/fpinscala>.
  */
object ParserTypes {
  type MS = Map[String, String]
  type OMS = Option[MS]
  type Parser = String => (String, OMS)
}

trait Parsers

object Parser {}

trait CLIConfigParser

object CLIConfigParser extends Parsers {

  // Parser combinators. --- {{{

  lazy val Success: Parser = x => (x, Some(Map.empty))

  def or(p1: Parser, p2: Parser): Parser = { (x: String) =>
    {
      val (s1: String, newP: OMS) = p1(x)
      newP match {
        case Some(_) => (s1, newP)
        case None    => p2(x)
      }
    }
  }

  def many1(p1: Parser): Parser = { (x: String) =>
    {
      val (s1: String, newP: OMS) = p1(x)
      newP match {
        case Some(_) => many(p1)(x)
        case None    => (x, newP)
      }
    }
  }

  def many(p1: Parser): Parser = {
    ???
  }

  // --- }}}

  case object Newline extends CLIConfigParser {
    val get = "\n"
  }
  case class Comment(get: OMS) extends CLIConfigParser
  case class Config(get: OMS) extends CLIConfigParser
  case class SubConfig(get: OMS) extends CLIConfigParser

  def parse(s: String)(p: Parser): OMS = {
    ???
  }

}
