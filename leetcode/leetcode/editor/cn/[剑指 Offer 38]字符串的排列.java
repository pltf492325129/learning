//输入一个字符串，打印出该字符串中字符的所有排列。 
//
// 
//
// 你可以以任意顺序返回这个字符串数组，但里面不能有重复元素。 
//
// 
//
// 示例: 
//
// 输入：s = "abc"
//输出：["abc","acb","bac","bca","cab","cba"]
// 
//
// 
//
// 限制： 
//
// 1 <= s 的长度 <= 8 
// Related Topics 字符串 回溯 
// 👍 396 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
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
            return;
        }
        HashSet<Character> set = new HashSet<>();
        for (int i = x; i < c.length; i++) {
            if (set.contains(c[i])) continue;
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
}
//leetcode submit region end(Prohibit modification and deletion)
