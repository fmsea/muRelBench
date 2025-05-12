#!/usr/bin/env python

import sys
import os
from functools import partial, reduce


def apply(arg, fn):
    return fn(arg)


def compose(*fns):
    return partial(reduce, apply, fns)


def read_file(fname):
    with open(fname, 'r') as fh:
        return fh.readlines()


def write_file(fname, lines):
    with open(fname, 'w') as fh:
        for line in lines:
            fh.write(line)


def columns(line):
    cols = line.split(',')
    assert len(cols) == 10
    return cols


def benchmark(line=None, cols=None):
    if line is None and cols is None:
        return "Benchmark"
    elif cols is None:
        return columns(line)[0]
    else:
        return cols[0].strip('"')


def param_n(line=None, cols=None):
    if line is None and cols is None:
        return "Param: N"
    elif cols is None:
        return columns(line)[7]
    else:
        return cols[7]


def drop_package_from_benchmark_names(line):
    cols = columns(line)
    bench = benchmark(cols=cols).split('.')[-1]
    return ','.join(['"' + bench + '"'] + cols[1:])


def line_transforms(lines):
    def __transform__(lines):
        for line in lines:
            transform = compose(
                drop_package_from_benchmark_names
            )
            yield transform(line)
    return list(__transform__(lines))


def split_data(lines):
    table = {}
    header = lines[0]
    for line in lines[1:]:
        cols = columns(line)
        bench = benchmark(cols=cols)
        n = param_n(cols=cols)
        if bench not in table:
            table[bench] = {}
        if n not in table[bench]:
            table[bench][n] = []
        table[bench][n].append(line)

    return (header, table)


def splay(header, table, outprefix=None):
    if outprefix is None:
        outprefix = './'
    for benchmark in table.keys():
        for (n, lines) in table[benchmark].items():
            bench = benchmark.strip('"')
            fname = f'{bench}-n{n}.csv'
            write_file(os.path.join(outprefix, fname), [header] + lines)


def main(args):
    output_prefix = './'
    if len(args) > 1:
        output_prefix = args[1]
    lines = line_transforms(read_file(args[0]))
    (header, table) = split_data(lines)
    splay(header, table, output_prefix)


if __name__ == '__main__':
    main(sys.argv[1:])
