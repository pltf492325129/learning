package jianzhiOffer.day3;

public class _58reverWords {
    public String reverseWords(String s) {
        StringBuilder sb = new StringBuilder();
        //去除两端空格
        s.trim();
        //初始化,利用双指针
        int n = s.length();
        int j = n - 1, i = j;
        while (i >= 0) {
            while (i >= 0 && s.charAt(i) != ' ') i--;
            /**
             * 注意这里是j+1哦 [ )
             */
            sb.append(s.substring(i + 1, j + 1) + " ");
            while (i >= 0 && s.charAt(i) == ' ') i--;
            j = i;
        }
        return sb.toString().trim();
    }


    public String reverseWords1(String s) {
        int n = s.length();
        String[] sa = new String[n];
        int j = 0;
        for (int i = 0; i < n; i++) {
            //遇到不是空格的单词取出来放到数组里
            StringBuilder sb = new StringBuilder();
            while (s.charAt(i) != ' ') {
                sb.append(s.charAt(i));
                i++;
            }
            sa[j++] = sb.toString();
        }
        StringBuilder ss = new StringBuilder();
        for (int m = j; m > 0; m--) {
            ss.append(sa[m]);
        }
        ss.append(sa[0]);
        return ss.toString();
    }
}
