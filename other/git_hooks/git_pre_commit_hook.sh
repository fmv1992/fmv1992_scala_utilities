#! /bin/bash

export MAKEFLAGS='-j1'

# This git pre commit hook is intended to work on both cygwin and unix
# machines.
# It should be symlinked to ../../.git/hooks/pre-commit.

# Halt on error.
set -euo pipefail

# Go to execution directory.
# cd $(dirname $0)

make format
git diff --name-only --cached --diff-filter=ACMRTUXB \
    | xargs --verbose git add --force
# --diff-filter=ACDMRTUXB
#                 ↑
# Removed deleted parameter.

versionfile=./fmv1992_scala_utilities/src/main/resources/version
test -f $versionfile

make --jobs 1 dev
make --jobs 1 clean
make --jobs 1 test

# # Bump minor version.
# fileversion=$(find $versionfile -name 'version' -type f)
#
# # ???: Do not bump if version file is already added:
# # git diff --name-only --cached --diff-filter=AM
# tmpversion=$(mktemp)
# if verify_is_backwards_compatible.sh "$PWD" 0
# then
#     # Bump patch version.
#     cat "${fileversion}" | python3 -c "import sys ; i = sys.stdin.read(); j = i.split('.') ; j[2] = str(int(j[2]) + 1) ; print('.'.join(j), end='')" > "$tmpversion"
# else
#     # Bump minor and reset patch.
#     cat "${fileversion}" | python3 -c "import sys ; i = sys.stdin.read(); j = i.split('.') ; j[-1] = '0'; j[1] = str(int(j[1]) + 1) ; print('.'.join(j), end='')" > "$tmpversion"
# fi
# mv "$tmpversion" "${fileversion}"
# git add -f "$fileversion"

# `verify_is_backwards_compatible.sh` was once displayed here.

echo "Do not forget to run: 'stty sane'"

# vim: set filetype=sh fileformat=unix nowrap:
