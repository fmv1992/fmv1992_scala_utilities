package fmv1992.fmv1992_scala_utilities.util

// From <https://stackoverflow.com/a/33786312/5544140>.

import reflect.macros.blackbox.Context

import language.experimental.macros

import scala.io._

object S {

  def putfile_impl(c: Context)(file: c.Expr[String]): c.Expr[String] = {
    import c._, universe._
    file.tree match {
      case Literal(Constant(s: String)) =>
        val res = Source.fromFile(s, "UTF-8").getLines.mkString("\n")
        c.Expr[String](Literal(Constant(res)))
    }
  }

  def putfile(file: String): String = macro S.putfile_impl

}
