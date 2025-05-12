# μRelBench #

A set of synthetic benchmarks for relational abstract domains and their
operations.

## Dependencies ###

The main requirements for muRelBench are OpenJDK version 11, and Maven 3.x.
Please install these tools through your preferred method.

If you use [Guix][guix], then please use the following command to load into a
temporary environment for building and developing muRelBench:

```bash
guix time-machine -C channels.scm -- shell -m manifest.scm
```

### Docker Image ###

Currently, the container image is built using [Guix Pack][guix-pack].
Therefore, without using [Guix][guix] it is not possible to build the container
image.  However, if you have OpenJDK 11 and Maven, then everything should work
all the same.

## Building ##

After installing dependencies, building the project is as simple as invoking
Maven:

```bash
mvn package
```

The `package` goal will create a `target/benchmarks.jar` file, which is the
main entry-point of executing benchmarks.

## Running ##

To execute the benchmarks, the following command should be sufficient:

```bash
java -jar target/benchmarks.jar
```

### Reproducing Results from Publication ###

The following command was used to produce the results for the linked
publication:

```bash
java -jar target/benchmarks.jar \
     --time-unit=ms \
     --result-format=csv \
     --result=/tmp/benchmarks.csv
```

This produces an output file `/tmp/benchmarks.csv` which can be parsed and
plotted using the files within the `scripts` directory.

### WIP: Workflow ###

While it is still work in progress, the `workflow.scm` file describes the
entire workflow using [Concise Common Workflow Language (ccwl)][ccwl].  The
[ccwl][ccwl] tool compiles down to [Common Workflow Language (cwl)][cwl], which
can then run the entire workflow using a [cwl][cwl] implementation.

# License #

The code is released AS-IS, WITHOUT warranty in the hopes that it will be
useful, under the terms and conditions of the GNU Public License version 3 (or
at your option) and later revision.  You should have received a copy of the
[license][GPL-v3] with the distribution of this package.  If not, it can be
found [online][GPL-v3] at https://www.gnu.org/licenses/gpl-3.0.en.html.

[GPL-v3]: https://www.gnu.org/licenses/gpl-3.0.en.html

[guix]: https://guix.gnu.org/

[ccwl]: https://ccwl.systemreboot.net/

[cwl]: https://www.commonwl.org/
