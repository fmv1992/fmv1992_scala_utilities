package fmv1992.fmv1992_scala_utilities.uniq

// From https://stackoverflow.com/a/33786312/5544140 --- {{{
import reflect.macros.Context

import language.experimental.macros

import scala.io._

// Entering paste mode (ctrl-D to finish)

class S(val c: Context) {
  import c._, universe._

  def smac(file: c.Expr[String]): c.Expr[String] = file.tree match {
    case Literal(Constant(s: String)) =>
      val res = Source.fromFile(s, "UTF-8").getLines.mkString("\n")
      c.Expr[String](Literal(Constant(res)))
  }

}

object S {

  def f(file: String): String = macro S.smac

}
