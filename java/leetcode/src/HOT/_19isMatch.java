package HOT;

import java.util.HashMap;

public class _19isMatch {
    public boolean isMatch(String s, String p) {
        return dp(s, 0, p, 0);
    }

    boolean dp(String s, int i, String p, int j) {
        HashMap<String, Boolean> memo = new HashMap<>();
        //终止条件
        int m = s.length();
        int n = p.length();
        if (i == m) {
            //p后边必须是 a*b*z* 形式
            if ((n - j) % 2 == 1) return false;
            for (; j + 1 < n; j += 2) {
                if (p.charAt(j+1) != '*') return false;
            }
            return true;
        } else if (j == n) {
            return i == m;
        }

        String key = i + "," + j;
        if (memo.containsKey(key)) return memo.get(key);
        boolean res = false;

        if (s.charAt(i) == p.charAt(j) || p.charAt(j) == '.') {
            //匹配
            /**
             * 一定要先判断越没越界，再进行操作，否则可能越界，IndexOutOfArray
             */
            if (j < n - 1 && p.charAt(j + 1) == '*') {
                //匹配 0 个字符  配多个字符
                /**
                 * 注意这里是 res = dp();
                 */
                res = dp(s, i, p, j + 2) || dp(s, i + 1, p, j);
            }else {
                //匹配一次
                res = dp(s, i + 1, p, j + 1);
            }
        }else {
            //不匹配
            if (j < n-1 && p.charAt(j + 1) == '*') {
                //匹配一次
                res = dp(s, i, p, j + 2);
            }else {
                //匹配0次
                res = false;
            }
        }
        memo.put(key, res);
        return res;
    }

    public boolean test(String s, String p) {
        int m = s.length();
        int n = p.length();
        int i = 0, j = 0;
        while (i < m && j < n) {
            if (s.charAt(i) == p.charAt(j) || p.charAt(j) == '.') {

                i++;
                j++;
            }else {

                return false;
            }
        }
        return i == j;
    }

    public static void main(String[] args) {
        _19isMatch isMatch = new _19isMatch();
        String s = "";
        String p = "c*a*";

        System.out.println(isMatch.dp(s, 0, p, 0));

    }
}


