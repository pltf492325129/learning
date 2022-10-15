package jianzhiOffer.queue;

public class _22getKthFromEnd {
    public ListNode getKthFromEnd(ListNode head, int k) {
        ListNode temp = head;
        for (int i = 0; i < k; i++) {
            head = head.next;
        }
        while (head != null) {
            temp = temp.next;
            head = head.next;
        }
        return temp;

    }
}
class ListNode {
     int val;
     ListNode next;
     ListNode(int x) { val = x; }
 }