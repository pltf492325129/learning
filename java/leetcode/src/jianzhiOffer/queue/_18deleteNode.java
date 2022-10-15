package jianzhiOffer.queue;

public class _18deleteNode {
    public ListNode deleteNode(ListNode head, int val) {
        ListNode temp = head;
        ListNode pre = head;
        while (temp.val != val) {
            pre = temp;
            temp = temp.next;
        }
        pre.next = temp.next;
        if (temp == head)return head.next;
        return head;
    }
}
