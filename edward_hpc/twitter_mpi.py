#!/usr/bin/env python
__author__ = 'Xin Huang'
"""
    Full Name: Xin Huang
    Student ID: 685269
"""
import argparse
import itertools
import time
import csv
from collections import Counter

from mpi4py import MPI

from func import conf, process_line


if __name__ == '__main__':
    # parse arguments
    parser = argparse.ArgumentParser()
    parser.add_argument('-k', default='it', help='keyword to search')
    args = parser.parse_args()

    conf.key = args.k

    comm = MPI.COMM_WORLD
    size = comm.Get_size()
    rank = comm.Get_rank()

    # process lines on each node with different indexes
    line_start = rank + 1

    # main function
    start_time = time.time()
    with open(conf.path, 'rb') as csv_file:
        reader = csv.reader(itertools.islice(csv_file, line_start, None, size), delimiter=',', quotechar='"')
        result = map(process_line, reader)

    # output
    print '[%s]Time: %s sec' % (rank, (time.time() - start_time))
    print '[%s]Rows: %s' % (rank, len(result))

    # merge result
    if rank != 0:
        comm.send(result, dest=0, tag=55)
    else:
        for i in range(1, size):
            data = comm.recv(source=i, tag=55)
            result.extend(data)

        print 'Comm Done Time: %s' % (time.time() - start_time)
        z_result = zip(*result)
        print 'K: %s, T: %s' % (conf.key, sum(z_result[0]))
        print 'Top 10 mentioned users: %s' % Counter(list(itertools.chain(*z_result[1]))).most_common(10)
        print 'Top 10 mentioned topics: %s' % Counter(list(itertools.chain(*z_result[2]))).most_common(10)
