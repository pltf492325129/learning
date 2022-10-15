package jianzhiOffer.day3;

public class _58_2reverWords {
    public String reverseLeftWords2(String s, int n) {
        StringBuilder sn = new StringBuilder();
        for (int i = n; i < s.length() + n; i++) {
            sn.append(s.charAt(i % s.length()));
        }
        return sn.toString();
    }
    public String reverseLeftWords(String s, int n) {
        String news = null;
        /**
         * 这里一定要注意子字符串的长度，为左闭又开 [ )
         */
        return news = s.substring(n, s.length() - 1) + s.substring(0, n - 1);
    }

}
