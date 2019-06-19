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
  type OMS = Option[MS]
  type Parser = String ⇒ (String, OMS)
}

object PrimitiveParsers {

  def newLine(s: String): (String, OMS) = {
    println("|" * 79)
    println(s)
    val newlines = s.takeWhile(_ == '\n')
    println(newlines)
    println("|" * 79)
    val rest = s.drop(newlines.size)
    if (newlines.isEmpty) {
      (s, None)
    } else {
      throw new Exception()
      (rest, Some(Map.empty))
    }
  }

  def comment(s: String): (String, OMS) = {
    val lines: List[String] = s.split("\n").toList
    val commentLines = lines.takeWhile(_.startsWith("#"))
    val otherLines = lines.drop(commentLines.length)
    if (commentLines.isEmpty) {
      (s, None)
    } else {
      (otherLines.mkString("\n"), Some(Map.empty))
    }
  }

  def kvp(s: String): (String, OMS) = {
    val lines: List[String] = s.split("\n").toList
    val nonSpaceLines = lines.takeWhile(
      x ⇒ (! x(0).isSpaceChar) && (!( x(0) == '#')))
    val otherLines = nonSpaceLines.drop(nonSpaceLines.length)
    (otherLines.mkString("\n"), Some(Map.empty))
    if (nonSpaceLines.isEmpty) {
      (s, None)
    } else {
      (otherLines.mkString("\n"), Some(Map.empty))
    }
  }

}

object CompoundedParsers {

  val lines: Parser = CLIConfigParser.many(PrimitiveParsers.newLine)

  val comments: Parser = CLIConfigParser.many(PrimitiveParsers.comment)

  val content: Parser = CLIConfigParser.many(PrimitiveParsers.kvp)

  val orThree: Parser = CLIConfigParser.or(
    lines,
    CLIConfigParser.or(
      comments,
      content))

  val many1Three: Parser = CLIConfigParser.raiseError(
    CLIConfigParser.many1(orThree))

}

trait Parsers

object Parser {}

trait CLIConfigParser

object CLIConfigParser extends Parsers {

  // Parser combinators. --- {{{

  def or(p1: Parser, p2: Parser): Parser = {
    println(p1)
    println(p2)
    (x: String) ⇒ {
        val (s1: String, newP: OMS) = p1(x)
        val res = newP match {
          case Some(_) ⇒ (s1, newP)
          case None ⇒ p2(x)
        }
        res
      }
  }

  def many1(p1: Parser): Parser = {
    (x: String) ⇒ {
        val (s1: String, newP: OMS) = p1(x)
        // println("-" * 79)
        // // println(p1)
        // // println(s1)
        // // println(x)
        // // println(newP)
        // newP.orElse(throw new Exception())
        // println("-" * 79)
        newP match {
          case Some(_) ⇒ many(p1)(s1)
          case None ⇒ (x, newP)
        }
      }
  }

  def many(p1: Parser): Parser = {
    (x: String) ⇒ {
        val (s1: String, newP: OMS) = p1(x)
        newP match {
          case Some(_) ⇒ many(p1)(s1)
          case None ⇒ (s1, newP)
        }
      }
  }

  def raiseError(p1: Parser): Parser = {
    (x: String) ⇒ {
        val (s1: String, newP: OMS) = p1(x)
        println("-" * 79)
        println(s1)
        println(newP)
        newP.orElse(throw new Exception())
        println("-" * 79)
        (s1, newP)
      }
  }

  // --- }}}

  case class Comment(get: OMS) extends CLIConfigParser
  case class Config(get: OMS) extends CLIConfigParser
  case class SubConfig(get: OMS) extends CLIConfigParser

  def parse(s: String)(p: Parser): OMS = {
    p(s)._2
  }

}
