package hotPractice;

import java.util.Arrays;

public class _455CookieDistribution {
    public int findContentChildren(int[] g, int[] s) {
        Arrays.sort(g);
        Arrays.sort(s);
        int res = 0;

        int numOfChi = g.length;
        int numOfCookies = s.length;


        for (int i =0, j =0; i < numOfChi && j < numOfCookies; i++, j++) {
            /**
             * 注意这里一定要先用while判断不符合的条件
             */
            while (j < numOfCookies && s[j] < g[i]) {
                j++;
            }
            if (j < numOfCookies) {
                res += 1;
            }
        }
        return res;
    }
    public static void main(String[] args) {
        int[] a = {1, 2};
        int[] b = {1, 2, 3};

        int[] a2 = {10, 9, 8, 7};
        int[] b2 = {5, 6, 7, 8};
        _455CookieDistribution cd = new _455CookieDistribution();
        System.out.println(cd.findContentChildren(a, b));
        System.out.println(cd.findContentChildren(a2, b2));
    }
}
