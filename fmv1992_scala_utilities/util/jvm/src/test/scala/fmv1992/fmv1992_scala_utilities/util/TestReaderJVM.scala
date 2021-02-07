package fmv1992.fmv1992_scala_utilities.util

import org.scalatest.funsuite.AnyFunSuite

// ???: This is a **CLASS** not an object...
class TestReaderJVM extends AnyFunSuite {

  test("Test exception.") {
    assertThrows[java.lang.NullPointerException](
      Reader
        .readLines("/tmp/unexisting_path_" + scala.util.Random.nextString(100))
        .toList,
    )
  }

}
