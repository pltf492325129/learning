package HOT;

import java.util.ArrayList;

public class _0_1knapsack {


    public static void main(String[] args) {

    }

    //W代表背包载重，N个物品
    public int knapsack(int W, int N, int[] wt, int[] val) {
        //考虑物品个数为0 背包容量为0 索引 N + 1
        int[][] dp = new int[W + 1][N + 1];
        for (int i = 0; i <= W; i++) {
            dp[i][0] = 0;
        }
        for (int i = 0; i <= N; i++) {
            dp[0][i] = 0;
        }
        //dp[i][w] 价值
        for (int i = 0; i <= N; i++) {
            for (int w = 0; w <= W; w++) {
                if (w - wt[i-1] < 0) {
                    //这里对应的都是 i - 1 对应第i个物品
                    dp[i][w] = dp[i-1][w];
                }
                else {
                    dp[i][w] = Math.max(dp[i - 1][w], dp[i - 1][w - wt[i]] + val[i-1]);
                }
            }
        }
        return dp[N][W];
    }

}

