#! /bin/bash

# This git pre commit hook is intended to work on both cygwin and unix
# machines.
# It should be symlinked to ../../.git/hooks/pre-commit.

# Halt on error.
set -e
set -x

# Go to execution directory.
# cd $(dirname $0)

git diff --name-only --cached --diff-filter=ACMRTUXB \
    | grep '\.scala' \
    | parallel \
        -I % \
        --verbose \
        --jobs $((2*$(nproc))) \
        "vim -i NONE -n -c 'VimScalafmt' -c 'noautocmd x!' %"
git diff --name-only --cached --diff-filter=ACMRTUXB \
    | xargs --verbose git add --force
# --diff-filter=ACDMRTUXB
#                 ↑
# Removed deleted parameter.

versionfile=./fmv1992_scala_utilities/src/main/resources/version
test -f $versionfile

make dev
make clean
make assembly
make test

# Bump minor version.
fileversion=$(find $versionfile -name 'version' -type f)

tmpversion=$(mktemp)
if verify_is_backwards_compatible.sh "$PWD" 0 >/tmp/verify.txt 2>&1
then
    # Bump patch version.
    cat "${fileversion}" | python3 -c "import sys ; i = sys.stdin.read(); j = i.split('.') ; j[2] = str(int(j[2]) + 1) ; print('.'.join(j), end='')" > "$tmpversion"
else
    # Bump minor and reset patch.
    cat "${fileversion}" | python3 -c "import sys ; i = sys.stdin.read(); j = i.split('.') ; j[-1] = '0'; j[1] = str(int(j[1]) + 1) ; print('.'.join(j), end='')" > "$tmpversion"
fi
mv "$tmpversion" "${fileversion}"
git add -f "$fileversion"

# `verify_is_backwards_compatible.sh` was once displayed here.

echo "Do not forget to run: 'stty sane'"

exit 1

# vim: set filetype=sh fileformat=unix nowrap:
