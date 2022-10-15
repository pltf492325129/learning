package hotPractice;

public class _55JumpGame {
    public boolean canJump(int[] nums) {
        if (nums == null) return false;
        /**
         * 从后往前的贪心算法，
         * */
        int endReachable = nums.length - 1;
        for (int i = nums.length - 1; i >= 0; i--) {
            if (nums[i] + i > endReachable) {
                /**
                 * 这里注意更新endReachable,它为下标
                 */
                endReachable = i;
            }
        }
        return endReachable == 0;
    }
}
