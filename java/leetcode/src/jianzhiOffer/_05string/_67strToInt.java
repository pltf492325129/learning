package jianzhiOffer._05string;

public class _67strToInt {
    public int strToInt(String str) {
        System.out.println(str);
        //注意这里并未改变str， 而是重新复制了一个字符串
        char[] chars = str.trim().toCharArray();
        if (chars.length == 0)return 0;
        int sign = 1;
        int i = 0;
        int sum = 0;
        int bndry = Integer.MAX_VALUE/10;
        while (i < chars.length) {
            //放符号位
            if (chars[0] == '-') {
                sign = -1;
                i++;
            }else if (chars[0] == '+') i++;
            //如果是数字则进行加法运算
            while (i < chars.length && chars[i] - '0' >= 0 && chars[i] - '0' <= 9) {
                int x = chars[i] - '0';
                if (sum > bndry || (sum == bndry && chars[i] > '7')) {
                    return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                }
                sum = sum * 10 + x;
                i++;
            }
            break;
        }
        return sign*sum;
    }

    public static void main(String[] args) {
        _67strToInt strToInt = new _67strToInt();
        int i = strToInt.strToInt("+1");
        System.out.println(i);
    }
}
