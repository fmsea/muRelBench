JARFILE=./target/benchmarks.jar

.PHONY: all
all: $(JARFILE) package

$(JARFILE):
	mvn --batch-mode package

.PHONY: package
package: $(JARFILE)
	guix pack --format=docker \
		--entry-point=bin/benchmarks.sh \
		--manifest=guix/benchmarks.scm
