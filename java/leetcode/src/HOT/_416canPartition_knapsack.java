package HOT;

public class _416canPartition_knapsack {
    public boolean canPartition(int[] nums) {
        int sum = 0;
        for (int num : nums) {
            sum += num;
        }
        if ((sum & 1) == 1) return false;
        int target = sum / 2;
        int m = nums.length;

        boolean[][] dp = new boolean[m][target+1];
        // 先填表格第 0 行，第 1 个数只能让容积为它自己的背包恰好装满 ????
        if (nums[0] <= target) {
            dp[0][nums[0]] = true;
        }
        for (int i = 1; i < m; i++) {
            for (int j = 0; j <= target; j++) {
                dp[i][j] = dp[i - 1][j];
                if (nums[i] == j) {
                    dp[i][j] = true;
                    continue;
                }
                if (nums[i] < j) {
                    dp[i][j] = dp[i - 1][j] || dp[i - 1][j - nums[i]];
                }
            }
        }
        return dp[m - 1][target];
    }
}
