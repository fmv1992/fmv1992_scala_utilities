// Renamed package according to sections "Packages and Imports" & "Creating
// a package".

// IMPORTANT: see "projectnote01".
package fmv1992.fmv1992_scala_utilities.util

import java.io.File
import java.io.FileNotFoundException

object Reader {

  /** Superseded by <https://www.scala-lang.org/api/2.13.4/scala/util/Using$.html>.
    */
  @deprecated("Deprecated. Use `scala.util.Using` instead.")
  def loanPattern[A](
      closeable: scala.io.Source,
  )(f: scala.io.Source => A): A = {
    try {
      f(closeable)
    } finally {
      closeable.close()
    }
  }

  // See:
  // https://stackoverflow.com/a/33972743/5544140
  //
  //    ```
  //    Using(Source.fromFile("file.txt")) { source => source.mkString }
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
  @deprecated("Deprecated. Use `scala.io.Source.fromFile` instead.")
  def readLines(path: String): Seq[String] = {
    val f = new File(path)
    readLines(f)
  }

  @deprecated("Deprecated. Use `scala.io.Source.fromFile` instead.")
  def readLines(f: File): Seq[String] = {
    val path = f.getCanonicalPath
    val bufSource: scala.io.Source = if (f.exists) {
      // Raises java.io.FileNotFoundException if it does not exist.
      scala.io.Source.fromFile(f)
    } else {
      if (Utilities.isScalaNative) {
        throw new FileNotFoundException(
          s"File '$path' does not exist. Scala Native does not support java resources.",
        )
      } else {
        val shortenedPath = path.slice(path.lastIndexOf("/"), path.length)
        scala.io.Source
          .fromInputStream(getClass.getResourceAsStream(shortenedPath))
      }
    }
    val res: List[String] = loanPattern(bufSource)(_.getLines().toList)
    // ???
    // require(res.knownSize >= 0)
    res
  }

}
