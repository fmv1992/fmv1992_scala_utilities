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
type MSS = Map[String, Map[String, String]]
type Parser = String ⇒ Result
}

trait Parsers


trait Result
case class Success(get: MSS) extends Result
object Failure extends Result

object Parser {

}

trait CLIConfigParser
object CLIConfigParser extends Parsers {


  // Parser combinators. --- {{{

  def or(p1: Parser, p2: Parser): Parser = {
     ???
  }

  def many1(p1: Parser): Parser = {
     ???
  }

  // --- }}}

  case object Newline extends CLIConfigParser {
    val get = "\n"
  }
  case class Comment(get: String) extends CLIConfigParser
  case class Config(get: MSS) extends CLIConfigParser
  case class SubConfig(get: MSS) extends CLIConfigParser

  def parse(s: String): MSS = {
    ???
  }

}
