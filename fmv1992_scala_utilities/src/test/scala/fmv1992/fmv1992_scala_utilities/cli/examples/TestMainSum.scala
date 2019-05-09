package fmv1992.fmv1992_scala_utilities.cli

import fmv1992.fmv1992_scala_utilities.util.Example

object TestSum extends CLIConfigTestableMain {

  val version = "0.0.0"

  val programName = "TestSum"

  val CLIConfigPath = Example.cli02Path

  def testableMain(args: Seq[Argument]): List[String] = {

    val res = args.foldLeft(0)((l, x) ⇒ {
      x match {
        case GNUArg("sum", _) ⇒ x.value.map(_.toInt).sum + l
        case _ ⇒ l
      }
    })

    List(res.toString)
  }
}

//  Run this in vim:
//
// vim source: 1,$-10s/=>/⇒/ge
//
// vim: set filetype=scala fileformat=unix foldmarker={,} nowrap tabstop=2 softtabstop=2 spell:
