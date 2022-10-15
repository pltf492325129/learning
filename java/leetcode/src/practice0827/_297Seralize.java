package practice0827;

import java.util.LinkedList;
import java.util.List;

public class _297Seralize {
    // Encodes a tree to a single string.
    String SEP = ",";
    String NULL = "#";
    LinkedList<Integer>  res;
    StringBuilder res2 = new StringBuilder();
    public String serialize(TreeNode root) {
        traverse2(root);
        res2.delete(res2.length() - 2, res2.length() - 1);
        return res2.toString();

    }
    public void traverse2(TreeNode root) {
        if (root == null) {
            res2.append(NULL).append(SEP);
            return;
        }
        res2.append(root.val).append(SEP);
        traverse2(root.left);
        traverse2(root.right);
    }

    String data = "1,2,#,4,#,#,3,#,#,";


    public TreeNode deserialize(LinkedList<String> nodes) {
        if (nodes.isEmpty()) {
            return null;
        }
        String first = nodes.removeFirst();
        if (first.equals(NULL)) return null;

        TreeNode root = new TreeNode(Integer.parseInt(first));
        root.left = deserialize(nodes);
        root.right = deserialize(nodes);
        return root;
    }

    public TreeNode deserialize(String data) {
        LinkedList<String> nodes = new LinkedList<>();
        for (String node : data.split(",")) {
            nodes.add(node);
        }
        TreeNode root = deserialize(nodes);
        return root;
    }

/*    public void traverse(TreeNode root) {
        if (root == null) {
            res.add(-1);
            return;
        }

        res.add(root.val);
        traverse(root.left);
        traverse(root.right);
    }*/


}
