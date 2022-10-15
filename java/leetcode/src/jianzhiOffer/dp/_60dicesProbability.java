package jianzhiOffer.dp;

import java.util.Arrays;

public class _60dicesProbability {
    public double[] dicesProbability(int n) {
        double[] dp = new double[6];
        Arrays.fill(dp, 1.0/6.0);
        //这里是 <= n
        for (int i = 2; i <= n; i++) {
            double[] temp = new double[5 * n + 1];
            //这里是dp.length
            for (int j = 0; j < dp.length; j++) {
                for (int k = 0; k < 6; k++) {
                    temp[j+k] += temp[j] * (1.0/6.0);
                }
            }
            dp = temp;
        }

        return dp;
    }
}
