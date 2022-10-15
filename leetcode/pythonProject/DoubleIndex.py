from typing import List, Optional

from main import ListNode


class Solution:

    def removeDuplicates(self, nums: List[int]) -> int:
        '''
        _26删除链表有序数组中的重复项
        :param nums:
        :return:
        '''
        if not nums:
            return 0
        slow, fast = 0, 1
        while fast < len(nums):
            if nums[fast] != nums[slow]:
                slow += 1
                nums[slow] = nums[fast]
            fast += 1
        return slow + 1

    def deleteDuplicates(self, head: Optional[ListNode]) -> Optional[ListNode]:
        if not head:
            return head
        slow, fast = head, head.next
        while True:
            if not fast:
                break
            if slow.val != fast.val:
                slow.next = fast
                slow = slow.next
            fast = fast.next
        slow.next = None
        return head

if __name__ == '__main__':
    h1 = ListNode(1)
    h2 = ListNode(1)
    h1.next = h2
    h3 = ListNode(2)
    h2.next = h3
    s = Solution()
    s.deleteDuplicates(h1)




