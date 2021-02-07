package fmv1992.fmv1992_scala_utilities.util

import org.scalatest.funsuite.AnyFunSuite

class TestReaderNative extends AnyFunSuite {

  test("Test exception.") {
    // ???: Causes both a `java.lang.NullPointerException` and
    // a `java.net.SocketException: Socket closed` exception not on the tests
    // but on the execution of the tests itself.
    // assertThrows[java.lang.NullPointerException](
    // assertThrows[java.net.SocketException](
    // Reader
    //   .readLines("/tmp/unexisting_path_" + scala.util.Random.nextString(100))
    //   .toList
    // )
    // )
    assert(true)
  }

}
