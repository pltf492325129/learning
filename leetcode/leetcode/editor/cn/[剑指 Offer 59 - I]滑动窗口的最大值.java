//给定一个数组 nums 和滑动窗口的大小 k，请找出所有滑动窗口里的最大值。 
//
// 示例: 
//
// 输入: nums = [1,3,-1,-3,5,3,6,7], 和 k = 3
//输出: [3,3,5,5,6,7] 
//解释: 
//
//  滑动窗口的位置                最大值
//---------------               -----
//[1  3  -1] -3  5  3  6  7       3
// 1 [3  -1  -3] 5  3  6  7       3
// 1  3 [-1  -3  5] 3  6  7       5
// 1  3  -1 [-3  5  3] 6  7       5
// 1  3  -1  -3 [5  3  6] 7       6
// 1  3  -1  -3  5 [3  6  7]      7 
//
// 
//
// 提示： 
//
// 你可以假设 k 总是有效的，在输入数组不为空的情况下，1 ≤ k ≤ 输入数组的大小。 
//
// 注意：本题与主站 239 题相同：https://leetcode-cn.com/problems/sliding-window-maximum/ 
// Related Topics 队列 滑动窗口 单调队列 堆（优先队列） 
// 👍 315 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int[] maxSlidingWindow(int[] nums, int k) {
        Deque<Integer> queue = new LinkedList<>();
        int left = 0, right= -1;
        int n = nums.length;
        if (n == 0 || k == 0) return new int[0];
        int[] result = new int[n - k + 1];
        int j = 0;
        for (int i = 0; i < n && right < n; i++) {
            right++;
            queue.add(nums[right]);
            //如果窗口的大小 = k 并且right < n,向右遍历
            while ((right - left) == (k - 1) && right < n) {
                int value = maxValue(queue);
                //放结果集
                result[j++] = value;
                queue.removeFirst();
                right++;
                if (right < n) {
                    queue.add(nums[right]);
                }
                left++;
            }
        }
        return result;
    }
    //获取窗口中的最大值
    public int maxValue(Deque<Integer> queue) {
        int max = Integer.MIN_VALUE;
        for (Integer integer : queue) {
            max = Math.max(integer, max);
        }
        return max;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
