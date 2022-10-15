package jianzhiOffer;

import java.util.HashMap;

public class _56singleNumbers {
    public int[] singleNumbers(int[] nums) {
        int z = 0;
        for (int num : nums) {
            z ^= num;
        }
        int m = 0;
        while ((z & m) == 0) {
            z <<= 1;
        }
        int x = 0, y = 0;
        for (int num : nums) {
            if ((num & m) == 1) x ^= num;
            else y ^= num;
        }
        return new int[]{x, y};
    }
}
