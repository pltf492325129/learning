package hotPractice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class test2 {
    public static void main(String[] args) {
        boolean a = isAnagram("anagram", "nagaram");
        System.out.println(a);
        boolean b = isAnagram("rat", "car");
        System.out.println(b);

        System.out.println(isAnagram2("anagram", "nagaram"));
        System.out.println(isAnagram2("rat", "car"));
    }

    public static boolean isAnagram(String s, String t) {
        //  1、排序长度是否一致，若一致就比较各个位置的元素
        //    2、使用hashtable来存放s中各个元素， 拿t中各个元素和hash表中的元素比较，
        //    若在表中，则该位置上的元素个数-1，如果为-1，则直接返回，直到比较完t中所有元素。

        char[] sarray = s.toCharArray();
        Arrays.sort(sarray);
        char[] tarray = t.toCharArray();
        Arrays.sort(tarray);
        return Arrays.equals(sarray, tarray);
    }
    public static boolean isAnagram2(String s, String t) {
        Map<Character, Integer> map = new HashMap<Character, Integer>();
        for (char c : s.toCharArray()) {
            System.out.println(c);
            Integer count = map.get(c);
            count = count == null ? 1 : ++count;
            map.put(c, count);
        }
        for (Character c : t.toCharArray()) {
            if (map.containsKey(c)) {
                Integer count = map.get(c);
                count = count - 1;
                if (count == -1) {
                    return false;
                }
                map.put(c, count);
            } else {
                return false;
            }

        }
        return true;
    }
}
