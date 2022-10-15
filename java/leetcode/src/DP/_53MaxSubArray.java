package DP;

public class _53MaxSubArray {
    public int maxSubArray(int[] nums) {
        //    注意这里有点逆向思维
        /**    最大子序和 = 当前元素最大 ，或者是包含之前最大*/
        //    dp[i] = max(dp[i-1]+nums[i], dp[i])
        //    初始化：dp[0] = 0
        int n = nums.length;
        if (n == 1) return nums[0];
        //int[] dp = new int[n];
        //dp[0] = 0;
        /**
         * 注意这里初始化时，是原数组；
         */
        int[] dp = nums;

        for (int i = 1; i < n; i++) {
            //dp[i] = Math.max(dp[i-1] + nums[i], dp[i]);
            dp[i] = Math.max(dp[i-1] + nums[i], nums[i]);
        }
        int dpMax = nums[0];
        for (int i = 0; i < n; i++) {
            dpMax = Math.max(dp[i], dpMax);
        }
        return dpMax;
    }

    public static void main(String[] args) {
        int[] nums = {-2,1,-3,4,-1,2,1,-5,4};

        _53MaxSubArray maxSubArray = new _53MaxSubArray();
        int res = maxSubArray.maxSubArray(nums);
        System.out.println(res);

        int[] nums2 = {5, 4, -1, 7, 8};
        int res2 = maxSubArray.maxSubArray(nums2);
        System.out.println(res2);

        int[] nums3 = {-2, -1};
        int res3 = maxSubArray.maxSubArray(nums3);
        System.out.println(res3);
    }
}
