package DP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class _120TriangleMinimumTotal {
    public int minimumTotal(List<List<Integer>> triangle) {
        //    dp[i][j] = min(dp[i+1][j], dp[i+1][j+1]) + a[i][j] dp[i][j]表示到第i行的最小和
        //    数学归纳法：
        //    初始化：最下边一行
        int m = triangle.size();
        int n = triangle.get(m-1).size();

        int[][] dp = new int[m][n];
        for (int i = 0; i < m; i++) {
            dp[m - 1][i] = triangle.get(m - 1).get(i);
        }

        for (int i = m-2; i >= 0; i--) {
            /**
             * 注意这里的 j <= i; 而不是<
             */
            for (int j = 0; j <= i; j++) {
                dp[i][j] = Math.min(dp[i + 1][j], dp[i + 1][j + 1]) + triangle.get(i).get(j);
            }
        }
        return dp[0][0];
    }

    public static void main(String[] args) {
        List<List<Integer>> triangle = new ArrayList<>();
        ArrayList<Integer> list1 = new ArrayList<>(Arrays.asList(2));
        ArrayList<Integer> list2 = new ArrayList<>(Arrays.asList(3,4));
        ArrayList<Integer> list3 = new ArrayList<>(Arrays.asList(6,5,7));
        ArrayList<Integer> list4 = new ArrayList<>(Arrays.asList(4,1,8,3));

        triangle.add(list1);
        triangle.add(list2) ;
        triangle.add(list3) ;
        triangle.add(list4) ;

        for (List listArray : triangle) {
            System.out.println(listArray);
            //for (Object obj: listArray) {
            //    System.out.println(obj);
            //}
        }


        _120TriangleMinimumTotal test = new _120TriangleMinimumTotal();
        int res = test.minimumTotal(triangle);
        System.out.println(res);

    }
}
