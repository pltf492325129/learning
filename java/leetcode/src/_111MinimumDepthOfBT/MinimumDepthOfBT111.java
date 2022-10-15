package _111MinimumDepthOfBT;


import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

public class MinimumDepthOfBT111 {
    public int minDepth(TreeNode root) {
        return BFS(root);
    }
    //    回溯算法
    public int BFS(TreeNode start) {
        Queue<TreeNode> q = new LinkedList<>();
        //Set<TreeNode> visited = new TreeSet<>();

        q.offer(start);
        //visited.add(start);

        int depth = 1;

        while (!q.isEmpty()) {
            int sz = q.size();
            //    将当前队列中得所有节点向四周扩散
            for (int i = 0; i < sz; i++) {
                TreeNode cur = q.poll();
                //判断是否是查找目标
                if (cur.left == null && cur.right == null) {
                    return depth;
                }
                if (cur.left != null) {
                    q.offer(cur.left);
                }
                if (cur.right != null) {
                    q.offer(cur.right);
                }
            }
            depth++;
        }
        return depth;
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
