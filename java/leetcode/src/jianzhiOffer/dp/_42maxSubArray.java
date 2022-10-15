package jianzhiOffer.dp;

public class _42maxSubArray {
    public int maxSubArray(int[] nums) {
        //dp[i]表示以nums[i]为结尾的连续子数组求最大和
        //dp[i] = dp[i-1] + nums[i]
        //此时dp[i-1]分情况讨论：
        //dp[i-1] > 0       dp[i] = dp[i-1] + nums[i];
        //dp[i - 1] <= 0    dp[i] = nums[i]
        if (nums.length == 0)return 0;
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        //注意这里要使用nums[0]
        int res = nums[0];
        for (int i = 1; i < nums.length; i++) {
            //这里省略了比较dp【i-1】 + nums[i] nums[i] < 0; nums[i]必须被选取
            dp[i] = Math.max(nums[i], dp[i-1] + nums[i]);
            res  = Math.max(res, dp[i]);
        }
        return res;
    }

    public static void main(String[] args) {
        int[] test = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        _42maxSubArray maxSubArray = new _42maxSubArray();
        int i = maxSubArray.maxSubArray(test);
        System.out.println(i);
    }
}
