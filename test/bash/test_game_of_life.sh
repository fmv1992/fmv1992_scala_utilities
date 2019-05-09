#! /bin/bash

# Halt on error.
set -e
curdir=$(readlink -f $PWD)

tempdir=$(mktemp -d)

java_abs=$(find . -iname "game_of_life.jar" -print0 | xargs -0 readlink -f )
java_name=$(basename $java_abs)

cd $tempdir && cp "$java_abs" .

java -jar "${java_name}" --version
java -jar "${java_name}" --help
java -jar "${java_name}" --seed 0 --ngames 15

wait

cd $curdir

rm -rf "$tempdir"

# vim: set filetype=sh fileformat=unix nowrap spell spelllang=en:
