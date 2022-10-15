package HOT;

import java.util.ArrayList;
import java.util.HashMap;

public class _322coinChange {
    int[] coins;
    int amount;
    HashMap<Integer, Integer> memo = memo = new HashMap<>();

    public int coinChange(int[] coins, int amount) {

        this.coins = coins;
        this.amount = amount;
        int[] dp = new int[amount + 1];
        return dp(amount);
    }

    //dp(n)代表所凑amout金额的硬币个数, amount代表要凑的值
    public int dp(int n) {
        if (memo.containsKey(n)) return memo.get(n);
        if (n == 0) return 0;
        if (n < 0) return -1;
        int res = Integer.MAX_VALUE;

        for (int coin : coins) {
            int subProblem = dp(n - coin);
            if (subProblem == -1) continue;
            // res = min(min, dp(n - val) + 1)
            res = Math.min(res, subProblem + 1);
        }

        memo.put(n, res > amount ? -1 : res);
        return memo.get(n);
    }

    public int coinChange2(int[] coins, int amount) {
        /**
         * 能使用贪心算法的原因时因为存在整除关系
         */
        int coinNums = 0;
        for (int i = coins.length - 1; i >= 0; i--) {
            int temp = amount / coins[i];
            if (temp > 0) {
                coinNums += temp;
                amount -= temp * coins[i];
            }
            /**
             * 考虑边界条件，币值大于给定的值，如币值2元， 目标是一元。
             */
            if (amount == 0) {
                return coinNums;
            }
        }
        return -1;
    }


    public static void main(String[] args) {
        int[] coins = {1};
        int amount = 1;
        int res = new _322coinChange().coinChange(coins, amount);
        System.out.println(res);
    }
}
