//给你一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？请你找出所有和为 0 且不重
//复的三元组。 
//
// 注意：答案中不可以包含重复的三元组。 
//
// 
//
// 示例 1： 
//
// 
//输入：nums = [-1,0,1,2,-1,-4]
//输出：[[-1,-1,2],[-1,0,1]]
// 
//
// 示例 2： 
//
// 
//输入：nums = []
//输出：[]
// 
//
// 示例 3： 
//
//    
//输入：nums = [0]
//输出：[]
// 
//
// 
//
// 提示： 
//
// 
// 0 <= nums.length <= 3000 
// -105 <= nums[i] <= 105 
// 
// Related Topics 数组 双指针 
// 👍 3301 👎 0


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public List<List<Integer>> threeSum(int[] nums) {

        List<List<Integer>> res = new ArrayList<>();
        if (nums == null || nums.length < 2) return res;

        Arrays.sort(nums);

        for (int k = 0; k < nums.length-2; k++) {
            if (nums[k] > 0) break;
            if (k > 0 && nums[k] == nums[k - 1]) continue;
            int target = -nums[k];
            int lo = k + 1;
            int hi = nums.length - 1;

            while (lo < hi) {
                if (nums[lo] + nums[hi] == target) {
                    res.add(new ArrayList<>(Arrays.asList(nums[k], nums[lo], nums[hi])));
                    lo++;hi--;
                    while (lo < hi && nums[lo] == nums[lo - 1]) lo++;
                    while (lo < hi && nums[hi] == nums[hi + 1]) hi--;
                } else if (nums[lo] + nums[hi] > target) {
                    hi--;
                } else if (nums[lo] + nums[hi] < target) {
                    lo++;
                }
            }
        }
        return res;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
