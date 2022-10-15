package jianzhiOffer._05string;

import java.util.HashMap;
import java.util.Map;

public class _20isNumber {
    public boolean isNumber(String s) {
        Map[] states = {
                new HashMap() {{put(' ', 0); put('s', 1); put('d', 2); put('.', 4); }},
                new HashMap() {{put('d', 2); put('.', 4); }},   //1
                new HashMap() {{put('d', 2); put('e', 5); put('.', 3); put(' ', 8);}},
                new HashMap() {{put('d', 3); put('e', 5); put(' ', 8);}},  //3
                new HashMap() {{put('d', 3);}},   //4
                new HashMap() {{put('d', 7); put('s', 6);}}, //5
                new HashMap() {{put('d', 7) ;}},    //6
                new HashMap() {{put('d', 7); put(' ', 8);}}, //7
                new HashMap() {{put(' ', 8) ;}} //8
        };
        int p = 0;
        char t;
        for (char c : s.toCharArray()) {
            if (c >= '0' && c <= '9') t = 'd';
            else if (c == '+' || c == '-') t = 's';
            else if (c == 'E' || c == 'e') t = 'e';
            else if (c == '.' || c == ' ') t =  c;
            else t = '?';
            if (!states[p].containsKey(t)) return false;
            p = (int)states[p].get(t);
        }
        return p == 2 || p == 3 || p == 5 || p == 8;
    }








    public boolean isNumber2(String s) {
        char[] c = s.trim().toCharArray();


        int sign = 1;
        int i = 1;
        if (c[0] == '-'){
            sign = -1;
        }else if (c[0] == '+') {
            sign = 1;
        }
        i++;
        //1数值
        int j = 0;
        while (i < c.length) {
            if (c[i] == 'e' || c[i] == 'E') {
                //子数组
                if (isLitteNumber(c) || isIntNumber(c)) {
                    //判断右边是不是整数
                    if (isIntNumber(c)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return true;
    }

    //2小数
    private boolean isLitteNumber(char[] c) {
        int i = 1;
        while (i < c.length) {
            if (c[i] - '0' >= 0 && c[i] - '0' <= 9) i++;
            if (i < c.length && c[i] == '.') {
                while (i < c.length) {
                    if ((c[i] - '0' >= 0 && c[i] - '0' <= 9) || c[i] == ' ') i++;
                }
                return false;
            }
            return false;
        }
        return true;
    }

    //3正数
    private boolean isIntNumber(char[] c) {
        int i = 1;
        while (i < c.length){
            if (c[i] - '0' >= 0 && c[i] - '0' <= 9) i++;
            else return false;
        }
        return true;
    }
}
