//输入两个递增排序的链表，合并这两个链表并使新链表中的节点仍然是递增排序的。 
//
// 示例1： 
//
// 输入：1->2->4, 1->3->4
//输出：1->1->2->3->4->4 
//
// 限制： 
//
// 0 <= 链表长度 <= 1000 
//
// 注意：本题与主站 21 题相同：https://leetcode-cn.com/problems/merge-two-sorted-lists/ 
// Related Topics 递归 链表 
// 👍 151 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 */
class Solution {
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
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
//leetcode submit region end(Prohibit modification and deletion)
