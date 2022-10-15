package DP;

public class _198Rob {
    public int rob(int[] nums) {
        //dp[i]表示i间屋子偷的钱
        // dp[i] = max(dp[i-1]+0, dp[i-2]+nums[i])
        // 判断1偷没偷  1没有偷 dp[2] = nums[i]
        //             1  偷了 dp[2] = dp[1]
        // 初始化 dp[2] = max(dp[1], dp[0]+nums[1])
        // dp[0] = 0, dp[1] = nums[0]
         int n = nums.length;
        int[] dp = new int[n + 1];
        dp[0] = 0;
        dp[1] = nums[0];

        for (int i = 2; i <= n; i++) {
            dp[i] = Math.max(dp[i-1]+0, dp[i-2]+nums[i-1]);
        }
        return dp[n];
    }

    public int rob2(int[] nums) {
        //dp[i][0,1]表示i间屋子偷的钱 [0,1]表示0偷 1没偷
        // dp[i][1] = max(dp[i-1][1], 0) + nums[i];
        // dp[i][0] = max(dp[i-1][0], a[i-1][1]);
        // dp[1] = 0, dp[1] = nums[0]
        int n = nums.length;
        int[] dp = new int[n + 1];
        dp[0] = 0;
        dp[1] = nums[0];

        for (int i = 2; i <= n; i++) {
            dp[i] = Math.max(dp[i-1]+0, dp[i-2]+nums[i-1]);
        }
        return dp[n];
    }


    public static void main(String[] args) {
        int[] test = {1, 2, 3, 1};
        _198Rob rob = new _198Rob();
        System.out.println(rob.rob(test));

        int[] test2 = {2, 7, 9, 3, 1};
        System.out.println(rob.rob(test2));
    }
}
