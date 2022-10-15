package jianzhiOffer.day3;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class _38permutation {
    List<String> result = new LinkedList<>();
    char[] c;
    public String[] permutation(String s) {
        c = s.toCharArray();
        dfs(0);
        return result.toArray(new String[result.size()]);
    }
    public void dfs(int x) {
        if (x == c.length - 1) {
            result.add(String.valueOf(c));
        }
        HashSet<Object> set = new HashSet<>();
        for (int i = x; i < c.length; i++) {
            if (set.contains(c)) continue;
            set.add(c[i]);
            swap(i, x);
            dfs(x + 1);
            swap(x, i);
        }
    }
    void swap(int a, int b) {
        char tmp = c[a];
        c[a] = c[b];
        c[b] = tmp;
    }

    public String[] permutation3(String s) {
        //主要作用是为了剪枝，防止重复
        LinkedList<Character> track = new LinkedList<>();
        backtrack(s, track);
        /**
         * 这里可以直接将list链表直接转换为Strng数组；
         */
        return result.toArray(new String[result.size()]);
    }

    public void backtrack(String s, LinkedList<Character> track) {
        if (track.size() == s.length()) {
            result.add(linkedlist2String(track));
        }
        for (Character c : s.toCharArray()) {
            if (track.contains(c)) {
                continue;
            }
            track.add(c);
            backtrack(s, track);
            track.removeLast();
        }
    }


    public String linkedlist2String(List<Character> list) {
        StringBuilder sb = new StringBuilder();
        for (Object o : list) {
            sb.append(o.toString());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        _38permutation p = new _38permutation();

        String[] ress = p.permutation3("abb");
        for (String s : ress) {
            System.out.println(s);
        }

    }
}
