package fmv1992.fmv1992_scala_utilities.util

import org.scalatest.funsuite.AnyFunSuite

// ???: This is a **CLASS** not an object...
class TestReader extends AnyFunSuite {

  test("Test API.") {
    val assertionAnswer = "abc".map(_.toString)
    assert(Reader.readLines(Example.reader01Path).toList == assertionAnswer)
    assert(Reader.readLines(Example.reader01File).toList == assertionAnswer)
    // ???: Causes both a `java.lang.NullPointerException` and
    // a `java.net.SocketException: Socket closed` exception not on the tests
    // but on the execution of the tests itself.
    // assertThrows[java.lang.NullPointerException](
    //   Reader.readLines("/tmp/unexisting_path_" + Random.nextString(100)).toList
    // )
  }

  // test("Test exception.") {
  //   val assertionAnswer = "abc".map(_.toString)
  //   assert(Reader.readLines(Example.reader01Path).toList == assertionAnswer)
  //   assert(Reader.readLines(Example.reader01File).toList == assertionAnswer)
  //   // ???: Causes both a `java.lang.NullPointerException` and
  //   // a `java.net.SocketException: Socket closed` exception not on the tests
  //   // but on the execution of the tests itself.
  //   // assertThrows[java.lang.NullPointerException](
  //   assertThrows[java.net.SocketException](
  //     Reader
  //       .readLines("/tmp/unexisting_path_" + Random.nextString(100))
  //       .toList
  //   )
  //   // )
  // }

}
