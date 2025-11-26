from collections import deque

class Buffer:

    def __init__(self, buffer_size=30):
        self.buffer_size = buffer_size
        self.queue = deque(maxlen=buffer_size)
    
    def add(self, frame):
        self.queue.append(frame)

    def get_all(self):
        return list(self.queue)

