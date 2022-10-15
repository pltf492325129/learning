//给定一个非空整数数组，除了某个元素只出现一次以外，其余每个元素均出现两次。找出那个只出现了一次的元素。 
//
// 说明： 
//
// 你的算法应该具有线性时间复杂度。 你可以不使用额外空间来实现吗？ 
//
// 示例 1: 
//
// 输入: [2,2,1]
//输出: 1
// 
//
// 示例 2: 
//
// 输入: [4,1,2,1,2]
//输出: 4 
// Related Topics 位运算 哈希表 
// 👍 1894 👎 0


import java.util.HashMap;
import java.util.Map;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int singleNumber(int[] nums) {
        //    使用hashtable存储数字，和出现的次数
        Map<Integer, Integer> map = new HashMap<>();
        for (int i : nums) {
            Integer count = map.get(i);
            count = count == null ? 1 : ++count;
            map.put(i, count);
        }
        for (Integer i : map.keySet()) {
            Integer count = map.get(i);
            if (count == 1) {
                return i;
            }
        }
/*        for (int i = 0; i < nums.length; i++) {
            Integer count = map.get(nums[i]);
            count = count == null ? 1 : ++count;
            map.put(nums[i], count);
        }
        for (int j = 0; j < nums.length; j++) {
            Integer count = map.get(nums[j]);
            if (count == 1) {
                return nums[j];
            }
        }*/
        return -1;
        //    遍历表，找出只出现一次的数字

    }
}
//leetcode submit region end(Prohibit modification and deletion)
