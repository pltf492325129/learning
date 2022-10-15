package jianzhiOffer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class _06reversePrint {
    ArrayList<Integer> tmp = new ArrayList<>();
    public int[] reversePrint(ListNode head) {
        dfs(head);
        int[] res = new int[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            res[i] = tmp.get(i);
        }
        return res;
    }

    void dfs(ListNode head) {
        if (head == null) return;
        dfs(head.next);
        tmp.add(head.val);
    }

    ListNode reverse(ListNode head) {
        if (head == null || head.next == null) return null;
        ListNode tail = reverse(head.next);
        //这里是将下一个节点指向自己
        head.next.next = head;
        //将该节点断开
        head.next = null;
        return tail;
    }

}
class ListNode {
      int val;
      ListNode next;
      ListNode(int x) { val = x; }
}