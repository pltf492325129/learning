package HOT;

public class _206reverseList {
    private static ListNode testNode = new ListNode(1);
    public static ListNode testHead;

    public ListNode reverseList(ListNode head) {
        ListNode l, r;
        //注意初始化时 l = null;
        l = null;
        r = head;
        while (r != null) {
            ListNode temp = r.next;
            r.next = l;
            l = r;
            r = temp;
        }
        return l;
    }

    public ListNode reverseList2(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode last = reverseList(head.next);
        head.next.next = head;
        head.next = null;
        return last;
    }


    public static void main(String[] args) {
        int[] a = {1,2,3,4,5};
        int n = a.length;
        ListNode dummyRoot = new ListNode(0);
        ListNode ptr = dummyRoot;
        for (int i : a) {
            ptr.next = new ListNode(i);
            ptr = ptr.next;
        }

        ListNode cur = dummyRoot.next;
        while (cur != null) {
            System.out.println(cur.val);
            cur = cur.next;
        }

    }


}

class ListNode {
    int val;
    ListNode next;

    public ListNode(int val) {
        this.val = val;
    }
    public ListNode() {

    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}


