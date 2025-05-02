JARFILE=./target/benchmarks.jar

.PHONY: all
all: $(JARFILE) workflow.cwl package

$(JARFILE):
	mvn --batch-mode package

%.cwl: %.scm
	ccwl compile $< > $@

%.dot: %.scm
	ccwl compile --to=dot $< > $@

%.svg: %.dot
	dot -Tsvg -o$@ $<

.PHONY: package
package: $(JARFILE)
	guix pack --format=docker \
		--entry-point=bin/benchmarks.sh \
		--manifest=guix/benchmarks.scm

.PHONY: clean
clean:
	-rm $(JARFILE) \
		workflow.cwl
