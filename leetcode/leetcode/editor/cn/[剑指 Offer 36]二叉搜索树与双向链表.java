/*
// Definition for a Node.
class Node {
    public int val;
    public Node left;
    public Node right;

    public Node() {}

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val,Node _left,Node _right) {
        val = _val;
        left = _left;
        right = _right;
    }
};
*/
class Solution {
    Node pre,head;

    public Node treeToDoublyList(Node root) {
        if (root == null) return null;
        dfs(root);
        //初始化有点重要， 把首尾连接起来
        head.left = pre;
        pre.right = head;
        return head;
    }

    public void dfs(Node cur) {
        if (cur == null) return;
        dfs(cur.left);
        if (pre != null) pre.right = cur;    // 1 -> 2
        // 为啥这里head = cur??? 考虑为空嘛，赋初值，head
        else head = cur;
        cur.left = pre;
        pre = cur;
        dfs(cur.right);
    }
}