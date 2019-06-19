package fmv1992.fmv1992_scala_utilities.cli

import org.scalatest.FunSuite

import fmv1992.fmv1992_scala_utilities.util.Example

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

class TestNewParser extends FunSuite {

  val t1 = """
  |# Comment.
  |
  |Name: name.
  |
  |Name2: name2.
  |
  |# Comment2.
  |
  """.trim.stripMargin

  def ParserChar(c: Char)(x: String) =
    if (x.startsWith(c.toString))
      (x.slice(1, x.length), Some(Map.empty: ParserTypes.MS))
    else (x, None)

  // test("Test API.") {
  // println(
  // CLIConfigParser.parse(t1)(CompoundedParsers.many1Three)
  // )
  // }

  test("Test many1.") {
    val parserAs = CLIConfigParser.many1(ParserChar('a'))
    // assertThrows[scala.IllegalArgumentException](
    val parsedOK = parserAs("aaa")
    parsedOK._2.orElse(throw new Exception())

    val parsedError = parserAs("xaaaa")
    assert(! parsedError._2.isDefined)
    // assert(! parsedError._1 == "X")
    // parsedError._2.orElse(throw new Exception())
  }

  test("Test newlines.") {
    val p = CompoundedParsers.lines("\n\n \n \n  \n")
    assert(p._1 == " \n \n  \n")
    p._2.orElse(throw new Exception())
  }

  test("Test or.") {
    val parseAorB = CLIConfigParser.many1(
      CLIConfigParser.or(ParserChar('a'), ParserChar('b'))
    )
    val parsedOK = parseAorB("abaaaaba")
    assert(parsedOK._1.isEmpty)
    parsedOK._2.orElse(throw new Exception())

    val parsedError = parseAorB("abaaaabaX")
    assert(parsedError._1 == "X")
    parsedError._2.orElse(throw new Exception())
  }

}
