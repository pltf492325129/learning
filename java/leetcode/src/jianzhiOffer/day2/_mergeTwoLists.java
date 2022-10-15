package jianzhiOffer.day2;


public class _mergeTwoLists {
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode dum = new ListNode(0);
        ListNode cur = dum;
        /**
         * 这里虚拟的dum节点非常关键,相当于头节点,而cur则是循环时的当下指针
         */
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                cur.next = l1;
                l1 = l1.next;
            }else {
                cur.next = l2;
                l2 = l2.next;
            }
            cur = cur.next;
        }
        cur.next = l1 != null ? l1 : l2;
        return dum.next;
    }
    public ListNode mergeTwoLists2(ListNode l1, ListNode l2) {
        ListNode l1head = l1.next;
        ListNode l2head = l2.next;
        ListNode head = new ListNode(0);
        if (l1head.val <= l2head.val){
            head = l1head;
        }else head = l2head;
        while (l1head != null && l2head != null) {
            if (l1head.val <= l2head.val) {
                l1head = l1head.next;
            }else {
                ListNode tmp = l1head.next;
                l1head.next = l2head;
                l2head = l2head.next;
                l1head = tmp;
            }
        }
        if (l1head == null) {
            l2head = l2head.next;
        } else if (l2head == null){
            l1head = l1head.next;
        }
        return head;
    }
}


class ListNode {
    int val;
    ListNode next;
    ListNode(int x) { val = x; }
}