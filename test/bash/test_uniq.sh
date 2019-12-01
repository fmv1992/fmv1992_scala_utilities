#! /bin/bash

# Halt on error.
set -e
curdir=$(readlink -f $PWD)

tempdir=$(mktemp -d)

java_abs=$(find . -iname "uniq.jar" -print0 | xargs -0 readlink -f )
java_name=$(basename $java_abs)

randomstring=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 30)†

cd $tempdir && cp "$java_abs" .

java -jar "${java_name}" --version
java -jar "${java_name}" --help
diff \
    <(echo -e 'a\nb\na' | java -jar "${java_name}" --unique | sha1sum -) \
    <(echo -e 'a\nb' | sha1sum -)

# Test an upper bound for memory (1 less megabyte causes error).
timeout 10s seq 0 500000 | sed -E "s|^|${randomstring}|g" | (_JAVA_OPTIONS="-Xms1m -Xmx111m" java -jar "${java_name}" --unique)

# Test memory constrained execution: no unjustified references to objects.
printf 'a\nb\n%.0s' $(seq 0 500000) \
    | (_JAVA_OPTIONS="-Xms1m -Xmx10m" java -jar "${java_name}" --unique)

# Test that the program streams lines instead of waiting for input exhaustion.
# ???: Unsatisfactory. See:
diff \
    <(echo "a") \
    <(timeout 1s bash -c "(echo a ; sleep 3s) | java -jar "${java_name}" --unique")

cd $curdir

rm -rf "$tempdir"

# vim: set filetype=sh fileformat=unix nowrap spell spelllang=en:
