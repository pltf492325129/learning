package practice0827;

public class _116 {
    public Node connect(Node root) {
        if (root == null) return null;
        traverse(root.left, root.right);
        return root;
    }

    public void traverse(Node node1, Node node2) {
        if (node1 == null || node2 == null) return;
        node1.next = node2;
        traverse(node1.left, node1.right);
        traverse(node2.left, node2.right);
        traverse(node1.right, node1.left);
    }

    public void trasverse(Node root) {
        if (root == null || root.left == null ) return;

        root.left.next = root.right;
        trasverse(root.left);
        trasverse(root.right);

    }
}

class Node {
    int val;
    Node left;
    Node right;
    Node next;

}