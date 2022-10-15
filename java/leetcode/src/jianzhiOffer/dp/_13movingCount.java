package jianzhiOffer.dp;

public class _13movingCount {
    public int movingCount(int m, int n, int k) {
        //dp[i][j] 表示i,j能到达的最大格子
        // dp[i][j] = dp[i-1][j], dp[i][j] + nums[i][j]
        // i/10 + i % 10 + j/10 + j%10 <= k
        // dp[0][j] = jp; dp[i][0] = i;

        return 0;
    }
}
