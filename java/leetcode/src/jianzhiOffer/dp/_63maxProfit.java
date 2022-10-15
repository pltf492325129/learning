package jianzhiOffer.dp;

public class _63maxProfit {
    public int maxProfit(int[] prices) {
        //dp[i] 表示以prices[i]结尾的子数组的最大利润, 第i天利润是第i-1天最大利润，
        // 或者是第i日卖出的最大利润，
        //dp[i] = max(dp[i-1] , max(prices[i] - min(prices[0:i]))
        //dp[0]=0;
        if (prices.length == 0 )return 0;

        int[] dp = new int[prices.length];
        int res = 0;
        //这里的最小值为第一个数
        int min = prices[0];
        dp[0] = 0;
        for (int i = 1; i < prices.length; i++) {
            min = Math.min(min, prices[i]);;
            dp[i] = Math.max(dp[i - 1], prices[i] - min);
            res = Math.max(res, dp[i]);
        }
        return res;

    }

    public static void main(String[] args) {
        int[] test2 = {7, 1, 5, 3, 6, 4};
        int[] test = {1,2};
        _63maxProfit maxProfit = new _63maxProfit();
        int i = maxProfit.maxProfit(test);
        System.out.println(i);
    }
}
