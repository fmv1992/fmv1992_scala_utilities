package fmv1992.fmv1992_scala_utilities.cli

import java.io.File

import fmv1992.fmv1992_scala_utilities.util.Reader
import fmv1992.fmv1992_scala_utilities.util.Utilities

/** CLI parser most general trait.
  *
  * @define parseDoc Parse a sequence of strings into a sequence of
  * [[Argument Arguments]].
  *
  */
trait CLIParser {

  /** $parseDoc */
  def parse(args: Seq[String]): Seq[Argument]

}

/** Configuration file based CLI parser. */
trait ConfigFileParser extends CLIParser {

  /** Map of parsed options.
    *
    * Example:
    *
    * ```
    * name: debug
    * n: 0
    * type: int
    * help: Help text.
    * ```
    *
    * Gets transformed into this:
    *
    * Map(debug -> Map(n -> 0, type -> int, help -> Help text.),
    * verbose -> Map(n -> 0, type -> int, help -> Help text.))
    *
    */
  val format: Map[String, Map[String, String]]

  /** $parseDoc */
  def parse(args: Seq[String]): Seq[Argument] = {

    /** Recursive parse. */
    def go(
        goArgs: Seq[String],
        acc: Seq[Argument]
    ): Seq[Argument] = {

      goArgs match {
        case Nil ⇒ acc
        case h :: t ⇒ {
          val name = h.stripPrefix("--")
          require(format.contains(name), format.keys + name)
          val n = format(name)("n").toInt
          val values = t.take(n)
          val newArg: Argument = GNUArg(name, values)
          val newList: Seq[Argument] = newArg +: acc
          go(goArgs.drop(n + 1), newList)
        }
      }
    }

    val parsedArgs: Seq[Argument] = go(args, Nil).reverse
    val argsLongNames = parsedArgs.map(_.longName)

    // Add default arguments if there is any.
    val defaultKeys: Seq[String] =
      format.filter(x ⇒ x._2.contains("default")).keys.toSeq
    val notIncludedDefaultKeys: Seq[String] = (defaultKeys diff argsLongNames)
    require(
      argsLongNames.intersect(notIncludedDefaultKeys).isEmpty,
      argsLongNames + "|" + notIncludedDefaultKeys
    )
    val additionalArgs: Seq[Argument] = {
      notIncludedDefaultKeys.map(x ⇒ GNUArg(x, List(format(x)("default"))))
    }

    parsedArgs ++ additionalArgs

  }

}

/** Standard parser. */
case class StandardParser(format: Map[String, Map[String, String]])
    extends ConfigFileParser {}

/** Companion object for StandardParser. */
object StandardParser {

  def apply(f: File): StandardParser = {
    def go(
        rest: List[String],
        acc: Map[String, String]
    ): Map[String, String] = {
      val parsedM: Map[String, String] = rest match {
        case Nil ⇒ acc
        case h :: t ⇒ {
          // ???: Allow the char ':' to be present in string.
          val Array(pre, post): Array[String] = h.split(":").map(_.trim)
          go(t, acc ++ (Map((pre, post)): Map[String, String]))
        }
      }
      parsedM
    }
    def isValidLine(x: String): Boolean = !(x.isEmpty || x.trim.startsWith("#"))

    val lines: List[String] =
      Reader.readLines(f).dropWhile(!isValidLine(_)).toList

    val isFilledLines = lines.map(isValidLine)
    // val groupSize = isFilledLines.takeWhile(identity(_)).length
    // val validLines = lines.zip(isFilledLines).filter(_._2).map(_._1)
    val indexes = Utilities.getContiguousElementsIndexes(isFilledLines)
    val validIndexes = indexes.filter(x ⇒ isFilledLines(x._1))
    val groupedLines = validIndexes.map(x ⇒ lines.slice(x._1, x._2))

    // Test that all groups have the same size: this is not a requirement
    // anymore.

    val res = groupedLines.map(go(_, (Map(): Map[String, String])))
    // Nest the map.
    val nested = res.map(x ⇒ {
      val name = x("name")
      Map((name, x - "name"))
    })
    val folded =
      nested.foldLeft(Map(): Map[String, Map[String, String]])((l, x) ⇒ l ++ x)
    StandardParser(folded)
  }

  def apply(s: String): StandardParser = {
    apply(new File(s))
  }

}

/** GNU Compliant argument parser.
  *
  * @see [[https://www.gnu.org/prep/standards/html_node/Command_002dLine-Interfaces.html]]
  */
case class GNUParser(format: Map[String, Map[String, String]])
    extends ConfigFileParser {

  require(format.contains("help"), format + " has to contain entry 'help'.")
  require(
    format.contains("version"),
    format + " has to contain entry 'version'."
  )

}

/** Companion object for GNUParser. */
object GNUParser {

  def apply(f: File): GNUParser = {
    GNUParser(StandardParser(f).format)
  }

  def apply(s: String): GNUParser = {
    apply(new File(s))
  }

}

/** General trait for an argument. */
trait Argument {

  val longName: String

  val value: Seq[String]

}

/** General implementation for an argument. */
case class Arg(longName: String, value: Seq[String]) extends Argument {}

/** GNU style argument. Requirements are assured by the parser. */
case class GNUArg(longName: String, value: Seq[String]) extends Argument {

  // ???: Enforce gnu argument names and coherence inside package.

}

/** Companion object for GNUArg. */
object GNUArg {}

