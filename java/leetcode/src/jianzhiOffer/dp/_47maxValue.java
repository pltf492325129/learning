package jianzhiOffer.dp;

public class _47maxValue {
    public int maxValue(int[][] grid) {
        // dp[i][j] 表示到达第i,j个格子所能拿到的最大值
        // dp[i][j] = dp[i-1][j] + nums[i][j] || dp[i][j-1] + nums[i][j]
        // dp[i][0]  dp[0][j]初始化
        int m = grid.length;
        int n = grid[0].length;
        int[][] dp = new int[m][n];
        for (int i = 0; i < m; i++) {
            dp[i][0] = grid[i][0];
        }
        for (int j = 0; j < n; j++) {
            dp[0][j] = grid[0][j];
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 && j == 0)continue;
                if (i == 0){
                    dp[i][j] = grid[i][j] + dp[i][j-1];
                } else if (j == 0) {
                    dp[i][j] = grid[i][j] + dp[i-1][j];
                } else{
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]) + grid[i][j];
                }
            }
        }
        return dp[m-1][n-1];
    }

    public static void main(String[] args) {
        int[][] test = {
                {1,3,1},
                {1,5,1},
                {4,2,1}
        };
        _47maxValue maxValue = new _47maxValue();
        int i = maxValue.maxValue(test);
        System.out.println(i);

    }
}
