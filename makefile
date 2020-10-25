# Set variables.
SHELL := /bin/bash
ROOT_DIR := $(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))

# Find all scala files.
SBT_FILES := $(shell find ./ -iname "build.sbt")
SCALA_FILES := $(shell find $(dir $@) -iname '*.scala')
SBT_FOLDERS := $(dir $(SBT_FILES))

export PROJECT_NAME ?= $(notdir $(ROOT_DIR))

export _JAVA_OPTIONS := -Xms3072m -Xmx6144m

# Build files.
FINAL_TARGET := ./fmv1992_scala_utilities/target/scala-2.12/root.jar

# Test files.
BASH_TEST_FILES := $(shell find . -name 'tmp' -prune -o -iname '*test*.sh' -print)

# Set scala compilation flags.
# SCALAC_CFLAGS = -cp $$PWD:$(ROOT_DIR)/code/my_scala_project/

# ???: Google drive link to download ~/.sbt needed to compile this project.
# https://drive.google.com/open?id=1FoY3kQi52PWllwc3ytYU9452qJ4ack1u

all: dev test assembly publishlocal doc coverage $(FINAL_TARGET)

format:
	find . \( -iname '*.scala' -o -iname '*.sbt' \) -print0 \
            | xargs --verbose -0 \
                scalafmt --config ./fmv1992_scala_utilities/.scalafmt.conf
	cd $(PROJECT_NAME) && sbt 'scalafix'

doc:
	cd $(dir $(firstword $(SBT_FILES))) && sbt doc

clean:
	find . -iname 'target' -print0 | xargs -0 rm -rf
	find . -path '*/project/*' -type d -prune -print0 | xargs -0 rm -rf
	find . -iname '*.class' -print0 | xargs -0 rm -rf
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
test: test_sbt test_bash

test_bash: $(FINAL_TARGET) $(BASH_TEST_FILES)

test_sbt:
	cd $(PROJECT_NAME) && sbt '+ test'

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

test%.sh: .FORCE
	bash -xv $@


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
        --tty \
        --entrypoint '' \
        $(PROJECT_NAME) \
        $(if $(DOCKER_CMD),$(DOCKER_CMD),bash)

docker_test:
	DOCKER_CMD='make test' make docker_run

# --- }}}

.FORCE:

# .EXPORT_ALL_VARIABLES:

.PHONY: all clean test doc test_sbt test_bash

# vim: set noexpandtab foldmethod=marker fileformat=unix filetype=make nowrap foldtext=foldtext():
