import os
import time
import threading
import requests


# Exercise 6 - A decorator for benchmarking
class benchmark(object):
    def __init__(self, warmups=0, iter=1, verbose=False, csv_file=None):
        self.warmups = warmsup
        self.iter = iter
        self.verbose = verbose
        self.csv_file = csv_file
        
    def __call__(self, func, *args, **kwargs):
        def wrapper(*args, **kwargs):

            times_w = []
            times_t = []

            # warmup rounds
            for _ in range(self.warmups):
                before = time.time()
                func(*args, **kwargs)
                after = time.time()
                times_w.append(after - before)

            # iteration times
            for _ in range(self.iter):
                before = time.time()
                func(*args, **kwargs)
                after = time.time()
                times_t.append(after - before)

            print(times)

        return wrapper


# Exercise 7 - Testing the decorator with multithreading
@benchmark
def test(f):
    # TODO
    pass


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
            if self.pre is not None:
                r = requests.get(self.pre, allow_redirects=True)
                with open(self.path_pre, 'wb') as fpre:
                    fpre.write(r.content)
                os.system("python " + self.path_pre)

            func(*args, **kwargs)

            if self.post is not None:
                r = requests.get(self.post, allow_redirects=True)
                with open(self.path_post, 'wb') as fpost:
                    fpost.write(r.content)
                os.system("python " + self.path_post)

        return wrapper


# fibonacci
def fib(n):
    print(n)
    if n == 0 or n == 1:
        return 1
    else:
        return fib(n - 1) + fib(n - 2)


pre_url = "http://pages.di.unipi.it/corradini/Didattica/AP-19/PROG-ASS/02/pre.py"
post_url = "http://pages.di.unipi.it/corradini/Didattica/AP-19/PROG-ASS/02/post.py"


@prepost(pre=pre_url, post=post_url)
def fib_it(n):
    a = 1
    b = 1
    c = 1
    for i in range(1, n):
        a = b
        b = c
        c = a + b
    print(c)


if __name__ == "__main__":
    fib_it(10)

