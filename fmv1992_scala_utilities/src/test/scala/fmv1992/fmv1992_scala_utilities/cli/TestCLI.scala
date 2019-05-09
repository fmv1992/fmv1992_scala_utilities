package fmv1992.fmv1992_scala_utilities.cli

import org.scalatest.FunSuite

import fmv1992.fmv1992_scala_utilities.util.Example
import fmv1992.fmv1992_scala_utilities.util.TestUtility

class TestStandardParser extends FunSuite {

  val defaultArgs = "--debug --verbose".split(" ").toList

  test("Most basic test: test the idea.") {
    val parsed = Example.cli01Parser.parse(defaultArgs)
    assert(parsed == List(GNUArg("debug", Nil), GNUArg("verbose", Nil)))
  }

  test("Test functionality with TestMainExample01.") {

    assert(
      TestMainExample01.testableMain(
        Example.cli01Parser.parse(defaultArgs.toList)
      ) ==
        """
        |Got debug flag.
        |Got verbose flag.""".stripMargin.trim.split("\n").toList
    )

    assert(
      TestSum.testableMain(
        Example.cli05Parser.parse("--sum 2 7".split(" ").toList)
      ) == List("9")
    )

    TestUtility.mockStdin(TestSum.main("--version".split(" ")), "")
    TestUtility.mockStdin(TestSum.main("--help".split(" ")), "")

  }

}

class TestGNUParser extends FunSuite {

  test("Test API.") {
    // Test valid instantiation.
    GNUParser(Example.cli02File)

    // Test invalid instantiation.
    assertThrows[scala.IllegalArgumentException](
      GNUParser(Example.cli03File)
    )

    assertThrows[scala.IllegalArgumentException](
      GNUParser(Example.cli04File)
    )

  }

  test("Test default keys.") {

    val parser = StandardParser(Example.cli06File)
    assert(!parser.parse(List()).isEmpty)

    val parsed = parser.parse(List("--execute", "ten"))
    assert(parsed(0).value(0) == "ten")

  }

}

//  Run this in vim:
//
// vim source: 1,$-10s/=>/⇒/ge
//
// vim: set filetype=scala fileformat=unix foldmarker={,} nowrap tabstop=2 softtabstop=2 spell:
