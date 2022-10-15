package DP;

public class _64MinPathSum {
    public int minPathSum(int[][] grid) {
        //    dp[i][j] i,j 分别代表第i行，第j列，dp[i][j]代表路径和
        //    dp[i][j] = min(dp[i-1][j], dp[i][j-1]) + a[i][j]
        //    初始化：机器人只能从上边往下走，或者机器人只能从左边往右边走
        //    dp[0][j] = arr[0][j] + dp[0][j-1]
        int m = grid.length;
        int n = grid[0].length;
        if (m <= 0 || n <= 0) return 0;

        int[][] dp = new int[m][n];
        dp[0][0] = grid[0][0];

        // 机器人只能从左边往右边走，初始化最上边的行
        /**
         * 注意这里的m n 对应的是行还是列，一定要分清楚
         */
        for (int i = 1; i < n; i++) {
            dp[0][i] = grid[0][i] + dp[0][i-1];
        }
        //初始化最最左边的列
        for (int i = 1; i < m; i++) {
            dp[i][0] = grid[i][0] + dp[i - 1][0];
        }

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = Math.min(dp[i-1][j], dp[i][j-1]) + grid[i][j];
            }
        }
        return dp[m-1][n-1];
    }

    public static void main(String[] args) {
        int[][] grid = {{1, 3, 1}, {1, 5, 1}, {4, 2, 1}};
        int[][] grid2 = {{1, 2, 3}, {4, 5, 6}};

        _64MinPathSum sol = new _64MinPathSum();
        //int res = sol.minPathSum(grid);
        int res2 = sol.minPathSum(grid2);


        //System.out.println(res);
        System.out.println(res2);

    }
}
