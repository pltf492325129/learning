package jianzhiOffer.bit;

public class _15hamingWeight {
    public int hammingWeight(int n) {
        String s = String.valueOf(n);
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '1') {
                count++;
            }
        }
        return count;
    }
    //第i位与上n进行运算，检查每一位是否为0
    public int hammingWeight2(int n) {
        int count = 0;
        for (int i = 0; i < 32; i++) {
            if ((n & (1 << i)) != 0) {
                count++;
            }
        }
        return count;
    }

    //n & (n-1)消除最右边的1

    public int hammingWeight3(int n) {
        int count = 0;
        while (n != 0) {
            count++;
            n = n & (n - 1);
        }
        return count;
    }
}
