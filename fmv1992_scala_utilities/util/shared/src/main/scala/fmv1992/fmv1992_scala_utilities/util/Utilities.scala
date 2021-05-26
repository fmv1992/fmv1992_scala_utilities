// Renamed package according to sections "Packages and Imports" & "Creating
// a package".

// IMPORTANT: see "projectnote01".
package fmv1992.fmv1992_scala_utilities.util

import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files

import scala.jdk.CollectionConverters._

object Utilities {

  def getContiguousElementsIndexes[A](l: Seq[A]): Seq[(Int, Int)] = {
    @scala.annotation.tailrec
    def go(
        gl: Seq[A],
        curIndex: Int,
        indexCounter: Int,
        curEl: A,
        acc: Seq[(Int, Int)],
    ): Seq[(Int, Int)] = {
      val nextIndexCounter = indexCounter + 1
      gl match {
        case h :: t => {
          if (curEl == h) {
            go(t, curIndex, nextIndexCounter, curEl, acc)
          } else {
            go(
              t,
              nextIndexCounter,
              nextIndexCounter,
              h,
              (curIndex, nextIndexCounter) +: acc,
            )
          }
        }
        case Nil => (curIndex, nextIndexCounter) +: acc
      }
    }
    if (l.isEmpty) Seq.empty else go(l.tail, 0, 0, l.head, Nil).reverse
  }

  def isScalaNative: Boolean = {
    scala.util.Properties.javaVmName.toLowerCase
      .filter(_.isLetter)
      .contains("scalanative")
  }

  def isScalaJVM: Boolean = {
    scala.util.Properties.javaVmName.toLowerCase
      .filter(_.isLetter)
      .contains("vm")
  }

  def getScalaVersion: (Int, Int, Int) = {
    val vs =
      scala.util.Properties.versionString.filter(x => x.isDigit || (x == '.'))
    val vint: Array[Int] = vs.split('.').map(x => x.toInt)
    vint match {
      case Array(a, b, c) => (a, b, c)
      case _              => throw new Exception(vint.toString)
    }
  }

  // ???: A `Seq` is not ideal here. A `Set` (with lazy evaluation) is better.

  private def isFile(p: Path): Boolean = !Files.isDirectory(p)

  private def isDirectory(p: Path): Boolean = !isFile(p)

  def findAllNodes(s: String): Seq[Path] = findAllNodes(Paths.get(s))

  def findAllNodes(pa: Path): Seq[Path] = {
    def go(pb: Path): Seq[Path] = {
      if (isFile(pb)) {
        return LazyList(pb)
      } else {
        val nodes: LazyList[Path] = Files.list(pb).iterator.asScala.to(LazyList)
        return nodes.filter(isFile) ++ nodes.filter(isDirectory) ++ nodes
          .filter(isDirectory)
          .map(go(_))
          .reduceOption(_ ++ _)
          .getOrElse(LazyList.empty)
      }
    }
    go(pa).map(_.toAbsolutePath)
  }

  def findAllFiles(pa: Path): Seq[Path] = {
    return findAllNodes(pa).filter(!Files.isDirectory(_))
  }

  def findAllDirectories(pa: Path): Seq[Path] = {
    return findAllNodes(pa).filter(Files.isDirectory(_))
  }

}
