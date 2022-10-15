package jianzhiOffer.dp;

public class _14cuttingRope {
    public int cuttingRope2(int n) {
        if (n <= 3) return n-1;
        int b = n % 3;
        int p = 1000000007;
        long rem = 1, x = 3;
        //注意这里a /= 2; 下边 x = x*x; 而且最后肯定得 a%2==1,不管是奇数还是偶数;
        for (int a = n / 3 - 1; a > 0; a /= 2) {
            if (a % 2 == 1) rem = (rem * x) % p;
            x = (x * x) % p;
        }
        if (b == 0) return (int) (rem * 3 % p);
        if (b == 1) return (int) (rem * 4 % p);
        return (int)(rem * 6 % p);

    }
    public int cuttingRope(int n) {
        if (n <= 3) return n-1;
        int a = n / 3;
        int b = n % 3;
        if (b == 0) return (int)Math.pow(3, a);
        if (b == 1) return (int)Math.pow(3, a-1) * 4;
        return (int)Math.pow(3,a) * 2;

    }
    public int cuttingRope3(int n) {
        //dp[i]表示长度为i最大的乘积
        // 如果剪j长度 dp[i] = max(dp[i-1] * j, j*(i-1));
        // 初始化 dp[2] = 1;
        int[] dp = new int[n + 1];
        dp[2] = 1;
        for (int i = 3; i < n + 1; i++) {
            for (int j = 2; j < i; j++) {
                dp[i] = Math.max(dp[i], Math.max(dp[i - j] * j, j * (i - j)));
            }
        }
        return dp[n];
    }


    public static void main(String[] args) {
        _14cuttingRope cuttingRope = new _14cuttingRope();
        int i = cuttingRope.cuttingRope(120);
        System.out.println(i);

    }

}
