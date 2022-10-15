package jianzhiOffer.dp;

public class _10numWays {
    public int numWays2(int n) {
        if (n <= 1 )return 1;
        int a = 1, b = 1, sum;
        for (int i = 0; i < n; i++) {
            sum = (a + b) % 1000000007;
            a = b;
            b = sum;
        }
        return a;
    }
    public int numWays(int n) {
        //dp[n] 表示有n种跳法
        //dp[n] = dp[n - 1] + dp[n - 2];
        //初始化 dp[2] = dp[1] + dp[0] = 1 + 1;
            if (n <= 1) return 1;

            int[] dp = new int[n + 1];
            dp[1] = 1;
            dp[0] = 1;

            for (int i = 2; i < n+1; i++) {
                dp[i] = dp[i - 1] + dp[i - 2];
            }
            //为啥46就不对了？？
            return dp[n] % 1000000007;
    }

    public static void main(String[] args) {
        _10numWays numWays = new _10numWays();
        int i = numWays.numWays(46);
        System.out.println(i);
    }
}
