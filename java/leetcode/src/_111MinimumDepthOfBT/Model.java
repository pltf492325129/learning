package _111MinimumDepthOfBT;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

public class Model {
    //    回溯算法
    public void BFS(TreeNode start, TreeNode target) {
        Queue<TreeNode> q = new LinkedList<>();
        Set<TreeNode> visited = new TreeSet<>();

        q.offer(start);
        visited.add(start);

        int depth = 1;

        while (!q.isEmpty()) {
            int sz = q.size();
            //    将当前队列中得所有节点向四周扩散
            for (int i = 0; i < sz; i++) {
                TreeNode cur = q.poll();
                //判断是否是查找目标
                if (cur == target) {
                    //return depth;
                }
                //cur相邻节点
/*                for (TreeNode node : cur.children()) {
                    if (!visited.contains(node)) {
                        q.offer(node);
                        visited.add(node);
                    }
                }*/
            }
        }
        depth++;
    }
}

