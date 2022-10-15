package _77Combination;

import java.util.LinkedList;
import java.util.List;

public class Combination {
    List<List<Integer>> res = new LinkedList<>();
    public List<List<Integer>> combine(int n, int k) {
        LinkedList<Integer> tracker = new LinkedList<>();
        backtracer(n, k,1, tracker);
        System.out.println(res);
        return res;
    }
    //    路径：track
    //    可选列表
    //    终止条件
    private void backtracer(int n, int k, int begin, LinkedList<Integer> tracker) {
        if (tracker.size() == k) {
            res.add(new LinkedList<>(tracker));
            return ;
        }
        for (int i = begin; i <= n; i ++){
            //做选择
            tracker.add(i);
            System.out.println("递归之前=》" + tracker);
            backtracer(n, k, i+1, tracker);
            // 撤销选择
            System.out.println("递归之后=》" + tracker);
            tracker.removeLast();
        }
    }


    public static void main(String[] args) {
        Combination combination = new Combination();
        combination.combine(4, 2);
    }
}
