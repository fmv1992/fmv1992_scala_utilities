// Renamed package according to sections "Packages and Imports" & "Creating
// a package".

// IMPORTANT: see "projectnote01".
package fmv1992.fmv1992_scala_utilities.util

import java.io.File
import java.io.FileNotFoundException

object Reader {

  // See:
  // https://stackoverflow.com/a/33972743/5544140
  //
  //    ```
  //    Using(Source.fromFile("file.txt")) { source ⇒ source.mkString }
  //    ```
  //
  // See:
  //
  // https://stackoverflow.com/questions/20762240/loaner-pattern-in-scala
  //
  //    Specially: https://stackoverflow.com/a/20765731/5544140
  //
  //        "Just make sure that all accesses to the resource are completed
  //        before exiting the block."
  //
  def readLines(path: String): Seq[String] = {
    val f = new File(path)
    readLines(f)
  }

  def readLines(f: File): Seq[String] = {
    val path = f.getCanonicalPath
    val res: Seq[String] = if (f.exists) {
      scala.io.Source.fromFile(f).getLines.toList
    } else {
      throw new FileNotFoundException(
        s"File '$path' does not exist. Scala Native does not support java resources."
      )
    }
    res
  }

}
