//给定一个不含重复数字的数组 nums ，返回其 所有可能的全排列 。你可以 按任意顺序 返回答案。 
//
// 
//
// 示例 1： 
//
// 
//输入：nums = [1,2,3]
//输出：[[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
// 
//
// 示例 2： 
//
// 
//输入：nums = [0,1]
//输出：[[0,1],[1,0]]
// 
//
// 示例 3： 
//
// 
//输入：nums = [1]
//输出：[[1]]
// 
//
// 
//
// 提示： 
//
// 
// 1 <= nums.length <= 6 
// -10 <= nums[i] <= 10 
// nums 中的所有整数 互不相同 
// 
// Related Topics 回溯算法 
// 👍 1412 👎 0

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
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
//leetcode submit region end(Prohibit modification and deletion)
