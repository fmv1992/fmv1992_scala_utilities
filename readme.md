# `fmv1992_scala_utilities`

*   `dev`:

    *   ![Build status](https://travis-ci.com/fmv1992/fmv1992_scala_utilities.svg?branch=dev)

    *   [![codecov](https://codecov.io/gh/fmv1992/fmv1992_scala_utilities/branch/dev/graph/badge.svg)](https://codecov.io/gh/fmv1992/fmv1992_scala_utilities)

*   `v1.x`: discontinued.

This project illustrates my journey in learning Scala and trying to deploy my first project for other people to use.

## How to compile

Unfortunately, [scalameta](https://repo1.maven.org/maven2/org/scalameta/) has not not published a version of `semanticdb` that is `2.13.4` compatible:

```
[error]   not found: /home/monteirobd/.ivy2/local/org.scalameta/semanticdb-scalac-core_2.13.4/4.3.20/ivys/ivy.xml
[error]   not found: https://repo1.maven.org/maven2/org/scalameta/semanticdb-scalac-core_2.13.4/4.3.20/semanticdb-scalac-core_2.13.4-4.3.20.
```

So one has to follow the [following](https://github.com/SemanticSugar/fmv1992_scala_utilities/blob/1d660e4e4dd24cc3db9bc5850e5547fec5684509/fmv1992_scala_utilities/build.sbt#L6):

1.  Apply the `base64` encoded patch:

    ```
    ZGlmZiAtLWdpdCBhL3Byb2plY3QvVmVyc2lvbnMuc2NhbGEgYi9wcm9qZWN0L1ZlcnNpb25zLnNjYWxhCmluZGV4IGNmMWU4ZGIuLjg2ZDI0YjcgMTAwNjQ0Ci0tLSBhL3Byb2plY3QvVmVyc2lvbnMuc2NhbGEKKysrIGIvcHJvamVjdC9WZXJzaW9ucy5zY2FsYQpAQCAtNCw3ICs0LDcgQEAgcGFja2FnZSBidWlsZAogb2JqZWN0IFZlcnNpb25zIHsKICAgdmFsIExhdGVzdFNjYWxhMjExID0gIjIuMTEuMTIiCiAgIHZhbCBMYXRlc3RTY2FsYTIxMiA9ICIyLjEyLjEyIgotICB2YWwgTGF0ZXN0U2NhbGEyMTMgPSAiMi4xMy4zIgorICB2YWwgTGF0ZXN0U2NhbGEyMTMgPSAiMi4xMy40IgogICB2YWwgTGVnYWN5U2NhbGFWZXJzaW9ucyA9CiAgICAgTGlzdCgiMi4xMi44IiwgIjIuMTIuOSIsICIyLjEyLjEwIiwgIjIuMTIuMTEiLCAiMi4xMy4wIiwgIjIuMTMuMSIsICIyLjEzLjIiKQogfQpkaWZmIC0tZ2l0IGEvcHJvamVjdC9idWlsZC5wcm9wZXJ0aWVzIGIvcHJvamVjdC9idWlsZC5wcm9wZXJ0aWVzCmluZGV4IDY1NGZlNzAuLjBiMmUwOWMgMTAwNjQ0Ci0tLSBhL3Byb2plY3QvYnVpbGQucHJvcGVydGllcworKysgYi9wcm9qZWN0L2J1aWxkLnByb3BlcnRpZXMKQEAgLTEgKzEgQEAKLXNidC52ZXJzaW9uPTEuMy4xMgorc2J0LnZlcnNpb249MS40LjcK
    ```

    to <https://github.com/scalameta/scalameta>

2.  Run `sbt publishLocal`.

3.  Copy the destination to `4.3.20`, e.g.: `cp -rf 4.3.20+0-ce628924+20210207-1837-SNAPSHOT 4.3.20`.


## Sub-projects

### CLI

Deprecated sub-project. It is not a project of its own: [scala_cli_parser](https://github.com/fmv1992/scala_cli_parser).

### Game of life

Deprecated sub-project.

### Uniq

Deprecated sub-project.

### Util

A very broad utilities package for Scala.

## TODO

*   Compare `./fmv1992_scala_utilities/build.sbt` to `43d2240` and fix the cross build.

<!--

*   Important commit: `b13d013d897bfbde2f6c94a6264cc3e5319078f6`: has no
    circular dependencies with other projects.

1.  Publish on a central repository. See:

    i.  <https://central.sonatype.org/pages/ossrh-guide.html#initial-setup>

    i.  <https://docs.scala-lang.org/overviews/contributors/index.html>

1.  Have 90% of code coverage.

1.  Document the code according to:

    1.  <https://docs.scala-lang.org/tour/packages-and-imports.html>

        *   ???: Use the `io.github.fmv1992` scheme.

    1.  <https://docs.scala-lang.org/style/scaladoc.html>

    1.  <https://docs.scala-lang.org/overviews/scaladoc/for-library-authors.html>

    *   Create a GNU style documentation.

    *   Move this readme to a documentation folder; create readme programatically.

1. Add references to `SICP` and `FPIS`.

-->

[comment]: # ( vim: set filetype=markdown fileformat=unix nowrap spell spelllang=en: )
