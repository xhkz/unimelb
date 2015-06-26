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
from multiprocessing import Pool

from func import conf, process_line


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-k', default='it', help='keyword to search')
    parser.add_argument('-p', default=None, type=int, help='number of processes')
    args = parser.parse_args()

    conf.key = args.k
    pool = Pool(args.p)

    # main function
    start_time = time.time()
    with open(conf.path, 'rb') as csv_file:
        reader = csv.reader(csv_file, delimiter=',', quotechar='"')
        next(reader)
        result = pool.map(process_line, reader)

    # output
    print 'Time: %s sec' % (time.time() - start_time)
    print 'Rows: %s' % len(result)
    z_result = zip(*result)
    print 'K: %s, T: %s' % (conf.key, sum(z_result[0]))
    print 'Top 10 mentioned users: %s' % Counter(list(itertools.chain(*z_result[1]))).most_common(10)
    print 'Top 10 mentioned topics: %s' % Counter(list(itertools.chain(*z_result[2]))).most_common(10)
