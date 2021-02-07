# Set variables.
SHELL := /bin/bash
ROOT_DIR := $(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))

export PROJECT_NAME ?= $(notdir $(ROOT_DIR))

# Find all scala files.
SBT_FILES := $(shell find $(PROJECT_NAME) -iname "build.sbt")
SCALA_FILES := $(shell find $(PROJECT_NAME) -iname '*.scala')
SBT_FOLDERS := $(dir $(SBT_FILES))

export SCALAC_OPTS := -Ywarn-dead-code
export _JAVA_OPTIONS ?= -Xms2048m -Xmx4096m

# Build files.
FINAL_TARGET := ./fmv1992_scala_utilities/target/scala-2.12/root.jar

# Set scala compilation flags.
# SCALAC_CFLAGS = -cp $$PWD:$(ROOT_DIR)/code/my_scala_project/

# ???: Google drive link to download ~/.sbt needed to compile this project.
# https://drive.google.com/open?id=1FoY3kQi52PWllwc3ytYU9452qJ4ack1u

all: dev test assembly publishlocal doc coverage $(FINAL_TARGET)

format:
	find . \( -iname '*.scala' -o -iname '*.sbt' \) -print0 \
        | xargs --verbose -0 \
            scalafmt --config ./fmv1992_scala_utilities/.scalafmt.conf
	cd $(PROJECT_NAME) && sbt -batch \
        'scalafix' \
        ';scalafixAll dependency:fix.scala213.ConstructorProcedureSyntax@com.sandinh:scala-rewrites:0.1.10-sd ;scalafixAll dependency:fix.scala213.ParensAroundLambda@com.sandinh:scala-rewrites:0.1.10-sd ;scalafixAll dependency:fix.scala213.NullaryOverride@com.sandinh:scala-rewrites:0.1.10-sd ;scalafixAll dependency:fix.scala213.MultiArgInfix@com.sandinh:scala-rewrites:0.1.10-sd ;scalafixAll dependency:fix.scala213.Any2StringAdd@com.sandinh:scala-rewrites:0.1.10-sd ;scalafixAll dependency:fix.scala213.ExplicitNonNullaryApply@com.sandinh:scala-rewrites:0.1.10-sd ;scalafixAll dependency:fix.scala213.ExplicitNullaryEtaExpansion@com.sandinh:scala-rewrites:0.1.10-sd'

doc:
	cd $(PROJECT_NAME) && sbt '+ doc'

clean:
	find . -iname 'target' -print0 | xargs -0 rm -rf
	find . -path '*/project/*' -type d -prune -print0 | xargs -0 rm -rf
	find . -iname '*.class' -print0 | xargs -0 rm -rf
	find . -iname '.bsp' -print0 | xargs -0 rm -rf
	find . -iname '.metals' -print0 | xargs -0 rm -rf
	find . -iname '.bloop' -print0 | xargs -0 rm -rf
	find . -iname '*.hnir' -print0 | xargs -0 rm -rf
	find . -type d -empty -delete

coverage:
	# ???: hack to build the report.
	exit_code=0 \
        && cd ./fmv1992_scala_utilities \
        && sbt clean coverage test \
        && (sbt coverageReport || true) \
        && sbt coverageAggregate \
        ; exit_code=$$? \
        ; echo "Report can be found on '$$(find . -iname "index.html")'." \
        ; exit "$${exit_code}"

# Test actions. --- {{{

# Killing a running process with:
#
#    SIGKILL                 → 137
#    SIGHUP                  → 129
#    CTRL+C                  → 127
#    `throw new Exception()` → 1
#    `require(1 == 2)`       → 1
#    With `make test`: timeout + `require(1 == 2)`          → 143
#    With `make test`: timeout + `throw new Exception()`    → 143
#    With `make test`: timeout + no errors or exceptions... → 143
#
# Also from the program `timeout`'s documentation:
#
# "If  the  command times out, and --preserve-status is not set, then exit with
# status 124."
#
# The logic above does not work because `scala` inside a shell script does not
# terminate with timeout (meaning it always timeouts).
test: docker_test test_host

test_host: test_sbt test_bash

test_bash: $(FINAL_TARGET)
	find ./test/bash/ -iname '*.sh' -print0 | xargs -0 -I % -n 1 -- bash -xv %

test_sbt:
	cd $(PROJECT_NAME) && sbt projects 2>&1 \
        | grep -E '.*info.*(JVM|Native)$$' \
        | sed -E 's/.* (\w+(Native|JVM))$$/\1/g' \
        | sort -u \
        | parallel --verbose --jobs 1 --halt now,fail=1 -I % -n 1 -- sbt '%/test'

# ???: This tasks fails erratically but succeeds after a few retries. See
# details here `fmv1992_scala_utilities:330cddf:readme.md:13`.
nativelink:
	cd $(PROJECT_NAME) && sbt projects 2>&1 \
        | grep -E 'Native$$' \
        | sed -E 's/.* (\w+Native)/\1/g' \
        | sort -u \
        | parallel --verbose --jobs 1 --halt now,fail=1 -I % -n 1 -- sbt '%/nativeLink'

compile: $(SBT_FILES) $(SCALA_FILES)
	cd $(PROJECT_NAME) && sbt '+ compile'

# --- }}}

# ???: make the assembly process general.
assembly: $(FINAL_TARGET)

publishlocal: .FORCE
	cd ./fmv1992_scala_utilities && sbt clean update '+ publishLocal'

dev:
	cp -f ./other/git_hooks/git_pre_commit_hook.sh ./.git/hooks/pre-commit || true
	cp -f ./other/git_hooks/git_pre_push.sh ./.git/hooks/pre-push || true
	chmod a+x ./.git/hooks/pre-commit
	chmod a+x ./.git/hooks/pre-push

$(FINAL_TARGET): $(SCALA_FILES) $(SBT_FILES)
	cd ./fmv1992_scala_utilities && sbt '+ assembly'
	touch --no-create -m $@

# Docker actions. --- {{{

docker_build:
	docker build \
        --file ./dockerfile \
        --tag $(PROJECT_NAME) \
        --build-arg project_name=$(PROJECT_NAME) \
        -- . \
        1>&2

docker_run:
	docker run \
        --interactive \
        --rm \
        --tty \
        --entrypoint '' \
        $(PROJECT_NAME) \
        $(if $(DOCKER_CMD),$(DOCKER_CMD),bash)

docker_test:
	DOCKER_CMD='bash -c "make clean && make test_host && make clean"' make docker_run
	DOCKER_CMD='bash -c "make clean && make nativelink && make clean"' make docker_run

# --- }}}

.FORCE:

# .EXPORT_ALL_VARIABLES:

.PHONY: all clean test doc test_sbt test_bash

# vim: set noexpandtab foldmethod=marker fileformat=unix filetype=make nowrap foldtext=foldtext():
