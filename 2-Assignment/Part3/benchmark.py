import os
import requests
import csv

from threading import Thread
from random import random
from time import time, sleep
from statistics import mean, variance


################################################################################
# Exercise 6 - A decorator for benchmarking
class benchmark(object):
    def __init__(self, warmups=0, iter=1, verbose=False, csv_file=None):
        self.warmups = warmups
        self.iter = iter
        self.verbose = verbose
        self.csv_file = csv_file

    def __call__(self, func, *args, **kwargs):
        def wrapper(*args, **kwargs):

            times = []

            # warmup rounds
            for i in range(self.warmups):
                before = time()
                func(*args, **kwargs)
                after = time()
                elapsed = after - before
                times.append([i + 1, True, elapsed])

                if self.verbose == True:
                    print("Warm-up round #{}/{}: {}ms".format(
                        i + 1, self.warmups, elapsed))

            # iteration times
            for i in range(self.iter):
                before = time()
                func(*args, **kwargs)
                after = time()
                elapsed = after - before
                times.append([i + 1, False, elapsed])

                if self.verbose == True:
                    print("Execution round #{}/{}: {}ms".format(
                        i + 1, self.iter, elapsed))

            # Benchmark information
            if self.csv_file == None:
                for t in times:
                    print("{},{},{}".format(t[0], t[1], t[2]))
            else:
                with open(self.csv_file, 'w') as csvfile:
                    writer = csv.writer(csvfile, quoting=csv.QUOTE_MINIMAL)
                    for t in times:
                        writer.writerow(t)

            # a small table is printed on the standard output
            # including the average time of execution and the variance
            if self.verbose == True:
                w_times = [t[2] for t in times if t[1] == False]
                i_times = [t[2] for t in times if t[1] == True]

                row_format = "{:>7}{:>7}{:>14.10}{:>14.10}"
                # warmup, rounds, mean, variance
                print(
                    row_format.format("warmup", "rounds", "mean", "variance"))
                print(
                    row_format.format(True, len(w_times), mean(w_times),
                                      variance(w_times)))
                print(
                    row_format.format(False, len(i_times), mean(i_times),
                                      variance(i_times)))

        return wrapper


# benchmark decorator example
@benchmark(warmups=3, iter=5, verbose=True, csv_file="benchmark.csv")
def random_sleep():
    sleep(random())


################################################################################
# Exercise 7 - Testing the decorator with multithreading
def test(f):
    def test_thread(nthread, ntimes):
        @benchmark(
            warmups=0,
            iter=1,
            verbose=False,
            csv_file="f_" + str(nthread) + "-" + str(ntimes))
        def wrapper():
            def caller(ntimes):
                for _ in range(ntimes):
                    f()

            pool = [
                Thread(target=caller, args=(ntimes, )) for _ in range(nthread)
            ]
            for t in pool:
                t.start()
            for t in pool:
                t.join()

        return wrapper

    # 16 times on a single thread
    test_thread(1, 16)()

    # 8 times on two threads
    test_thread(2, 8)()

    # 4 times on 4 threads
    test_thread(4, 4)()

    # 2 times on 8 threads
    test_thread(8, 2)()


# slow fibonacci example function
def fib(n=31):
    if n == 0 or n == 1:
        return 1
    else:
        return fib(n - 1) + fib(n - 2)


"""
> Discuss briefly the results in a comment in the Python file.

The results are:

$ cat f_1-16 
1,False,6.5086963176727295

$ cat f_2-8 
1,False,6.983638763427734

$ cat f_4-4 
1,False,6.9469075202941895

$ cat f_8-2 
1,False,7.051259517669678

As we can see increasing the number of threads (with a constant total number of call
to the function) we don't have any expected improvement.

This is due of the Python Global Interpreter Lock, that assures that only one thread
executes Python bytecode at a time.

The processes running fibonacci function are CPU-bounds so the GIL is never released
"""


################################################################################
# Exercise 8 - [Optional] Downloading and executing Python scripts
class prepost(object):
    def __init__(self,
                 pre=None,
                 post=None,
                 path_pre="/tmp/pre.py",
                 path_post="/tmp/post.py"):
        self.pre = pre
        self.post = post
        self.path_pre = path_pre
        self.path_post = path_post

    def __call__(self, func, *args, **kwargs):
        def wrapper(*args, **kwargs):

            # If selected pre script
            if self.pre is not None:
                # Download to temporary files using requests
                r = requests.get(self.pre, allow_redirects=True)
                with open(self.path_pre, 'wb') as fpre:
                    fpre.write(r.content)
                # Execute the script using os.system
                os.system("python3 " + self.path_pre)

            # Call function normally
            func(*args, **kwargs)

            # If selected post script
            if self.post is not None:
                # Download to temporary files using requests
                r = requests.get(self.post, allow_redirects=True)
                with open(self.path_post, 'wb') as fpost:
                    fpost.write(r.content)
                # Execute the script using os.system
                os.system("python3 " + self.path_post)

        return wrapper


# pre/post script urls
pre_url = "http://pages.di.unipi.it/corradini/Didattica/AP-19/PROG-ASS/02/pre.py"
post_url = "http://pages.di.unipi.it/corradini/Didattica/AP-19/PROG-ASS/02/post.py"


# prepost decorator example
@prepost(pre=pre_url, post=post_url)
def foo_prepost():
    print("Started")
    print("Finished")


if __name__ == "__main__":

    # Test Exercise 6
    print()
    print("Test Exercise 6")
    print()
    random_sleep()

    # Test Exercise 7
    print()
    print("Test Exercise 7")
    print()
    test(fib)

    # Test Exercise 8
    print()
    print("Test Exercise 8")
    print()
    foo_prepost()

