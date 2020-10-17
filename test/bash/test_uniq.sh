#! /bin/bash

# Halt on error.
set -e
curdir=$(readlink -f $PWD)

tempdir=$(mktemp -d)

java_abs=$(find . -iname "uniq.jar" -print0 | sort -z -u | tail -z -n 1 | xargs -0 readlink -f)
java_name=$(basename $java_abs)

cd $tempdir && cp "$java_abs" .

java -jar "${java_name}" --version
java -jar "${java_name}" --help
diff \
    <(echo -e 'a\nb\na' | java -jar "${java_name}" --unique | sha1sum -) \
    <(echo -e 'a\nb' | sha1sum -)

cd $curdir

rm -rf "$tempdir"

# vim: set filetype=sh fileformat=unix nowrap spell spelllang=en:
