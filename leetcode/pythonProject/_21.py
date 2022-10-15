from typing import Optional, List


class ListNode:
    def __init__(self, val, next=None):
        self.val = val
        self.next = next


class Solution:
    '''
    双指针在链表中的使用
    '''

    def mergeTwoLists(self, list1: ListNode, list2: ListNode) -> ListNode:
        '''
        两个链表合并成一个有序链表
        :param list1:
        :param list2:
        :return:
        '''
        dummy = ListNode(-1)
        p = dummy
        p1 = list1
        p2 = list2
        while p1 and p2:
            if p1.val > p2.val:
                p.next = p2
                p2 = p2.next
            else:
                p.next = p1
                p1 = p1.next
            p = p.next

        p.next = p1 if p1 is not None else p2
        return dummy.next

    def partition(self, head: ListNode, x: int) -> ListNode:
        '''
        将一个链表差分成两个链表
        :param head:
        :param x:
        :return:
        '''
        dummy1 = ListNode(-1)
        dummy2 = ListNode(-1)
        p1, p2 = dummy1, dummy2
        p = head
        while p:
            if p.val < x:
                p1.next = p
                p1 = p1.next
            else:
                p2.next = p
                p2 = p2.next
            p = p.next

        p1.next = dummy2.next
        p2.next = None  # **** 可以防止循环
        return dummy1.next

    def mergeKLists2(self, lists: List[Optional[ListNode]]) -> Optional[ListNode]:
        import queue
        pq = queue.PriorityQueue()
        for i in range(len(lists)):
            if lists[i]:
                pq.put(lists[i].val)
        p = dummy = ListNode(-1)
        while not pq.empty():
            node = pq.get()
            p.next = node
            if node:
                pq.put(node)
            p = p.next
        return dummy.next

    def removeNthFromEnd(self, head: Optional[ListNode], n: int) -> Optional[ListNode]:
        '''
        移除第倒数第k个节点
        :param head:
        :param n:
        :return:
        '''
        def find_k_node(head_node: ListNode, k: int):
            r, p = head_node, head_node
            for _ in range(k):
                p = p.next
            while p:
                r = r.next
                p = p.next
            return r
        dummy = ListNode(-1)
        dummy.next = head
        pnode = find_k_node(dummy, n + 1)
        pnode.next = pnode.next.next
        return dummy.next

    def middleNode(self, head: ListNode) -> ListNode:
        '''
        返回链表中点
        :param head:
        :return:
        '''
        fast, slow = head, head
        while fast and fast.next:
            fast = fast.next.next
            slow = slow.next
        if fast:
            return slow.next
        else:
            return slow


    def getIntersectionNode(self, headA: ListNode, headB: ListNode) -> Optional[ListNode]:
        p1, p2 = headA, headB
        while not p1 is p2:
            if p1:
                p1 = p1.next
            else:
                p1 = headB
            if p2:
                p2 = p2.next
            else:
                p2 = headA
        return p1  # 空指针时就==返回了

    def detectCycle(self, head: ListNode) -> ListNode:
        '''
        _22 offer 链表中环的入口节点  k-m
        :param head:
        :return:
        '''
        slow, fast = head, head
        while True:
            if not (fast and fast.next):
                return
            slow = slow.next
            fast = fast.next.next
            if slow == fast:
                break
        slow = head
        while slow != fast:
            slow = slow.next
            fast = fast.next
        return slow

if __name__ == '__main__':
    dummy = ListNode(-1)
    tnode = dummy
    node1 = ListNode(1)
    dummy.next = node1
    node2 = ListNode(2)
    node1.next = node2
    node3 = ListNode(3)
    node2.next = node3
    node4 = ListNode(4)
    node3.next = node4
    node5 = ListNode(5)
    node4.next = node5


    s = Solution()
    dy = s.removeNthFromEnd(dummy.next, 2)

    while dy:
        print(dy.val)
        dy = dy.next

