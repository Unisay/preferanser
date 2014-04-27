import urllib.request
import threading
import logging
from queue import Queue


logging.basicConfig(format='%(levelname)s | %(asctime)s | %(message)s', level=logging.DEBUG)

URL_FORMAT = 'http://trellis-club.com/ru/pref/history/%d/'


class FileGetter(threading.Thread):
    def __init__(self, i):
        self.i = i
        self.url = URL_FORMAT % self.i
        self.result = None
        threading.Thread.__init__(self)

    def get_result(self):
        return self.result

    def run(self):
        try:
            f = urllib.request.urlopen(self.url)
            self.result = f.read()
            f.close()
        except IOError:
            logging.error("Could not open URL: %s", self.url)


def get_urls(ids):
    def producer(q, ids):
        for i in ids:
            thread = FileGetter(i)
            thread.start()
            q.put(thread, True)

    finished = []

    def consumer(q, total_files):
        while len(finished) < total_files:
            thread = q.get(True)
            thread.join()
            content = thread.get_result()
            if content:
                logging.info('Fetched %s, len = %d' % (thread.url, len(content)))
                filename = 'data/%d.html' % thread.i
                f = open(filename, 'wb')
                f.write(content)
                f.close()
                logging.info('Written file %s' % filename)

    q = Queue(3)
    prod_thread = threading.Thread(target=producer, args=(q, ids))
    cons_thread = threading.Thread(target=consumer, args=(q, len(ids)))
    prod_thread.start()
    cons_thread.start()
    prod_thread.join()
    cons_thread.join()


get_urls(range(378, 10000))