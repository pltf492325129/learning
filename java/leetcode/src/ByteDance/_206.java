package ByteDance;

public class _206 {
    public ListNode reverseList(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode cur = reverseList(head.next);
        head.next.next = head;
        head.next = null;
        return cur;
    }
    public ListNode reverseList2(ListNode head) {
        ListNode l = null, r = head;
        while (r != null) {
            ListNode temp = r.next;
            l.next = r.next;
            l = r;
            r = temp;
        }
        return l;
    }
}
class ListNode{
    int val;
    ListNode next;
    public ListNode() {}
    public ListNode(int val) {this.val = val;}
    public ListNode(int val, ListNode next) {this.val = val; this.next = next;}

}
