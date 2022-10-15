package jianzhiOffer.day2;

public class _21reverseList {
    public ListNode reverseList(ListNode head) {
        return recur(head);
    }
    ListNode recur(ListNode head) {
        //注意这里head.next == null 时,也要返回head;

        if (head == null || head.next == null) return head;
        //这里返回pre,就是头节点
        ListNode pre = recur(head.next);
        head.next.next = head;
        head.next = null;
        return pre;
    }
}
