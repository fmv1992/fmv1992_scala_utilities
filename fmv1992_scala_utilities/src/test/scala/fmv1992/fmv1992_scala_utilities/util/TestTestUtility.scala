package fmv1992.fmv1992_scala_utilities.util

import org.scalatest.FunSuite

// Who tests the testers?
class TestTestUtility extends FunSuite {

  def mockProgramRunning(): Unit = {
    val in = scala.io.Source.stdin.getLines.toSeq
    val input = in.mkString("\n")
    Console.out.print(input.toUpperCase)
    Console.err.print(input.toLowerCase)
  }

}

//  Run this in vim:
//
// vim source: 1,$-10s/=>/⇒/ge
//
// vim: set filetype=scala fileformat=unix foldmarker={,} nowrap tabstop=2 softtabstop=2 spell:
