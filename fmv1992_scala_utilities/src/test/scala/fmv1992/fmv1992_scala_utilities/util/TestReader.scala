package fmv1992.fmv1992_scala_utilities.util

import scala.util.Random

import org.scalatest.FunSuite

// ???: This is a **CLASS** not an object...
class TestReader extends FunSuite {

  test("Test API.") {
    val assertionAnswer = "abc".map(_.toString)
    assert(Reader.readLines(Example.reader01Path).toList == assertionAnswer)
    assert(Reader.readLines(Example.reader01File).toList == assertionAnswer)
    assertThrows[java.lang.NullPointerException](
      Reader.readLines("/tmp/" + Random.nextString(100)).toList
    )
  }

}
