//给定一个包含非负整数的 m x n 网格 grid ，请找出一条从左上角到右下角的路径，使得路径上的数字总和为最小。 
//
// 说明：每次只能向下或者向右移动一步。 
//
// 
//
// 示例 1： 
//
// 
//输入：grid = [[1,3,1],[1,5,1],[4,2,1]]
//输出：7
//解释：因为路径 1→3→1→1→1 的总和最小。
// 
//
// 示例 2： 
//
// 
//输入：grid = [[1,2,3],[4,5,6]]
//输出：12
// 
//
// 
//
// 提示： 
//
// 
// m == grid.length 
// n == grid[i].length 
// 1 <= m, n <= 200 
// 0 <= grid[i][j] <= 100 
// 
// Related Topics 数组 动态规划 矩阵 
// 👍 919 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
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
        //初始化最最左边的列
        for (int i = 1; i < m; i++) {
            dp[i][0] = grid[i][0] + dp[i - 1][0];
        }
        // 机器人只能从左边往右边走，初始化最上边的行
        for (int i = 1; i < n; i++) {
            dp[0][i] = grid[0][i] + dp[0][i-1];
        }


        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = Math.min(dp[i-1][j], dp[i][j-1]) + grid[i][j];
            }
        }
        return dp[m-1][n-1];
    }
}
//leetcode submit region end(Prohibit modification and deletion)
