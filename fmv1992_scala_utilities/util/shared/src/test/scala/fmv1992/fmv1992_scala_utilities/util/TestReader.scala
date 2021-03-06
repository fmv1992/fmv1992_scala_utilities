package fmv1992.fmv1992_scala_utilities.util

import org.scalatest.funsuite.AnyFunSuite

// ???: This is a **CLASS** not an object...
class TestReader extends AnyFunSuite {

  test("Test API.") {
    val assertionAnswer = "abc".map(_.toString)
    assert(Reader.readLines(Example.reader01Path).toList == assertionAnswer)
    assert(Reader.readLines(Example.reader01File).toList == assertionAnswer)
  }

}
