package practice0827;

import java.util.ArrayList;
import java.util.List;

public class _366 {
    //private List<List<TreeNode>> res = new ArrayList<>();


    public int helper(TreeNode root, List<List<Integer>> res) {
        if (root == null) return -1;
        int left = helper(root.left, res);
        int right = helper(root.right, res);

        int depth = Math.max(left, right) + 1;
        if (res.size() < depth + 1) {
            res.add(new ArrayList<>());
        }
        res.get(depth).add(root.val);
        return depth;
    }
}
