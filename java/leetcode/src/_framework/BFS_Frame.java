package _framework;

import java.util.Queue;
import java.util.Set;
import java.util.Vector;

public class BFS_Frame {
    public int dfs(TreeNode start, TreeNode target) {
        //   回溯算法
        Queue<TreeNode> queue = null;
        Set<TreeNode> visited = null;

        int step = 0;
        //
        //queue.offer(start);
        //visited.add(start);
        //
        //while (queue != null) {
        //    int sz = queue.size();
        //
        //    for (int i = 0; i < sz; i++) {
        //        TreeNode cur = queue.poll();
        //
        //        if (cur == target) {
        //            return step;
        //        }
        //
        //        for (TreeNode x : cur.children) {
        //            if (!visited.contains(x)){
        //                queue.offer(x);
        //                visited.add(x);
        //            }
        //        }
        //    }
        //    step++;
        //}
        return step;
    }
}
