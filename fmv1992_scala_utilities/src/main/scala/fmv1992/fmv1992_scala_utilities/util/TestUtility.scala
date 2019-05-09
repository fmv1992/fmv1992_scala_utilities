// Renamed package according to sections "Packages and Imports" & "Creating
// a package".

// IMPORTANT: see "projectnote01".
package fmv1992.fmv1992_scala_utilities.util

import java.io.StringReader
import java.io.ByteArrayOutputStream

object TestUtility {

  // ???: Is this really necessary? We already have `testableMain`.
  def mockStdin[A](
      a: ⇒ A,
      stdin: Traversable[Char]
  ): (String, String, String) = {
    val input = stdin.mkString("")

    // ???: Could make it work with `withIn`.
    // See: https://gist.github.com/frgomes/d074cd48904f023ee0c4d676a68c2a9a
    val stream: java.io.InputStream = new java.io.ByteArrayInputStream(
      input.getBytes(java.nio.charset.StandardCharsets.UTF_8.name)
    )
    val stdinReader = new StringReader(input)
    // Could make it work sometimes with `setIn`.
    System.setIn(stream)

    val outCapture = new ByteArrayOutputStream
    val errCapture = new ByteArrayOutputStream
    Console.withIn(stdinReader) {
      Console.withOut(outCapture) {
        Console.withErr(errCapture) {
          a
        }
      }
    }
    (
      input,
      outCapture.toByteArray.map(_.toChar).mkString,
      errCapture.toByteArray.map(_.toChar).mkString
    )
  }

}

//  Run this in vim:
//
// vim source: 1,$-10s/=>/⇒/ge
//
// vim: set filetype=scala fileformat=unix foldmarker={,} nowrap tabstop=2 softtabstop=2 spell:
