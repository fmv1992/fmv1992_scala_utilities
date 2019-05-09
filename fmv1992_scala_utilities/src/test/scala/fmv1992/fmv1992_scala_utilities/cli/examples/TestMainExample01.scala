package fmv1992.fmv1992_scala_utilities.cli

import fmv1992.fmv1992_scala_utilities.util.Example

object TestMainExample01 extends CLIConfigTestableMain {

  val version = "0.0.0"

  val programName = "TestMainExample01"

  val CLIConfigPath = Example.cli01Path

  /** Testable interface for main program. */
  def testableMain(args: Seq[Argument]): List[String] = {

    val res = args
      .foldLeft(Nil: List[String])((l, x) ⇒ {
        x match {
          case GNUArg("debug", _) ⇒ "Got debug flag." +: l
          case GNUArg("verbose", _) ⇒ "Got verbose flag." +: l
          case _ ⇒ throw new Exception()
        }
      })
      .reverse

    res
  }
}

//  Run this in vim:
//
// vim source: 1,$-10s/=>/⇒/ge
//
// vim: set filetype=scala fileformat=unix foldmarker={,} nowrap tabstop=2 softtabstop=2 spell:
