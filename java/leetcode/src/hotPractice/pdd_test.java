package hotPractice;

public class pdd_test {
    private static int min;
    private static int[] dp;
    private static int i ;
    public static void main(String[] args) {
        //dp[i] 表示获得目标值的天数
        dp = new int[1001];
        dp[0] = 10;
        i = 0;
        min = 0;
        int res = fun(dp[0], 15);
        System.out.println(res);
    }


    private static int fun(int num1, int target) {
        while (dp[i] != target) {
            if (dp[i] == target) {
                min = Math.min(i, min);
            }
            dp[i++] = funCom(dp[i], i%4);
            fun(dp[i], target);
            dp[i--] = funCom2(dp[i], i%4);
        }
        return min;
    }

    private static int funCom(int num, int i) {
        if (i == 0) return num += -1;
        if (i == 1) return num += -2;
        if (i == 2) return num += 1;
        else return num *= 2;
    }
    private static int funCom2(int num, int i) {
        if (i == 0) return num -= -1;
        if (i == 1) return num -= -2;
        if (i == 2) return num -= 1;
        else return num /= 2;
    }

}
