package DP;

public class _143LCS {
    public int longestCommonSubsequence(String text1, String text2) {
        //    1、找数组dp[i][j]含义；dp[i][j] i,j分别代表两个字符串的长度， dp[i][j]代表LCS
        //    2、数学归纳法 if(tex1.charAt(i) == text2.charAt(j)) dp[i][j] = dp[i-1][j-1]
        //                  else dp[i][j] = max(dp[i-1][j], dp[i][j-1])
        //    3、找初始值
        /**
         * 这里将第一行和第一列都做0初始化，这样方便了初始化
         * 注意： for 的 <=   返回值是 dp[t1l][t2l]而不是dp[t1l-1][t2l-1]
         *        初始化数组大小为 new int[t1l + 1][t2l + 1]
         */
        int t1l = text1.length();
        int t2l = text2.length();

        int[][] dp = new int[t1l + 1][t2l + 1];


        for (int i = 1; i <= t1l; i++) {
            for (int j = 1; j <= t2l; j++) {
                if (text1.charAt(i-1) == text2.charAt(j-1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[t1l][t2l];
    }
}
