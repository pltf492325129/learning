# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import queue


class ListNode:
    def __init__(self, val, next=None):
        self.val = val
        self.next = next


def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    print_hi('PyCharm')
    a = ListNode(0)
    print(a.next)

    pq = queue.PriorityQueue()
    pq.put((2, 'code'))
    pq.put((1, 'dog'))
    pq.put((3, 'cat'))

    while not pq.empty():
        next_item = pq.get()
        print(next_item)

    t = [[1, 4, 5], [1, 3, 4], [2, 6]]
    pq2 = queue.PriorityQueue()

    for i in t:
        if i[0]:
            pq2.put(i[0])

    while not pq2.empty():
        print(pq2.get())


    a = [1, 1, 1, 4, 5]
    print(a[0] == a[1])
    print(a[0] != a[3])


