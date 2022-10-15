package jianzhiOffer.day3;

public class _05replaceSpace {
    public String replaceSpace(String s) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            //由于每次是一个字符替换成三个字符，所以这样还不可以，数组操作会更方便些。
            if (s.charAt(i) != ' '){
                /**
                 * res 使用的是 append方法
                 */
                res.append(s.charAt(i));
            }
            else if(s.charAt(i) == ' ') {
                res.append("%20");
            }
        }
        return res.toString();
    }
}
