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

  def putabspath_impl(c: Context)(file: c.Expr[String]): c.Expr[String] = {
    import c._, universe._
    file.tree match {
      case Literal(Constant(s: String)) =>
        val res = java.nio.file.Paths.get(s).toAbsolutePath.normalize
        val resAsString = res.toAbsolutePath.toString
        if (!res.toFile.exists) {
          val curdir = java.nio.file.Paths.get(".").toAbsolutePath.toString
          throw new Exception(
            s"Curdir: '${curdir}'\nPath: '${s}'\nAbspath: '${resAsString}'.",
          )
        }
        c.Expr[String](
          Literal(Constant(resAsString)),
        )
      case x => throw new Exception(x.toString)
    }
  }

  def putabspath(file: String): String = macro S.putabspath_impl

}
