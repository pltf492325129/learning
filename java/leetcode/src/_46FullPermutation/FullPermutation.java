package _46FullPermutation;

import java.util.*;

public class FullPermutation {
    List<List<Integer>> res = new LinkedList<>();

    public List<List<Integer>> permute(int[] nums) {
        LinkedList<Integer> track = new LinkedList<Integer>();
        backtrack(nums, track);
        return res;
    }
    //路径：记录在track中
    //选择列表：数字不包含在track中，
    //结束条件：track中包含nums中所有数字
    private void backtrack(int[] nums, LinkedList<Integer> track) {
        if (track.size() == nums.length) {
            res.add(new LinkedList<>(track));
            return;
        }
        for (int i = 0; i < nums.length; i ++) {
            //做选择
            //将该选择从选择列表中移除，将包含在track中的数字排除掉
            if (track.contains(nums[i])) continue;
            track.add(nums[i]);
            backtrack(nums, track);
            // 撤销选择
            track.removeLast();
        }
    }
}
