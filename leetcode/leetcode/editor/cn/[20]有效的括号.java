//给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串 s ，判断字符串是否有效。 
//
// 有效字符串需满足： 
//
// 
// 左括号必须用相同类型的右括号闭合。 
// 左括号必须以正确的顺序闭合。 
// 
//
// 
//
// 示例 1： 
//
// 
//输入：s = "()"
//输出：true
// 
//
// 示例 2： 
//
// 
//输入：s = "()[]{}"
//输出：true
// 
//
// 示例 3： 
//
// 
//输入：s = "(]"
//输出：false
// 
//
// 示例 4： 
//
// 
//输入：s = "([)]"
//输出：false
// 
//
// 示例 5： 
//
// 
//输入：s = "{[]}"
//输出：true 
//
// 
//
// 提示： 
//
// 
// 1 <= s.length <= 104 
// s 仅由括号 '()[]{}' 组成 
// 
// Related Topics 栈 字符串 
// 👍 2440 👎 0


import java.util.*;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public boolean isValid(String s) {
        int n = s.length();
        if (n % 2 == 1) {
            return false;
        }
        Map<Character, Character> paris = new HashMap<Character, Character>() {
            {
                put(')', '(');
                put('}', '{');
                put(']', '[');
            }
        };
        Deque<Character> stack = new LinkedList<Character>();
        for (int i = 0; i < n; i++) {
            char ch = s.charAt(i);
            if (paris.containsKey(ch)) {
                if (stack.isEmpty() || paris.get(ch) != stack.peek()) {
                    return false;
                }
                stack.pop();
            } else {
                stack.push(ch);
            }
        }
        return stack.isEmpty();



/*
//最关键的是把压栈，‘(’ 直接转换为 ‘）’
        Stack<Character> stack = new Stack<Character>();
        for (char c : s.toCharArray()) {
            if (c == '(')
                stack.push(')');
            else if (c == '{')
                stack.push('}');
            else if (c == '[')
                stack.push(']');
            else if (stack.isEmpty() || stack.pop() != c) {
                return false;
            }
        }
        return stack.isEmpty();*/

    }
/*        //遇到左括号就入栈，遇到右括号就出栈
        Stack<Character> stack = new Stack<Character>();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(' || s.charAt(i) == '{' || s.charAt(i) == '[') {
                stack.push(s.charAt(i));
            } else if (s.charAt(i) == ')'|| s.charAt(i) == '}' || s.charAt(i) == ']') {
                if (stack.peek() == '(' && s.charAt(i) == ')' && !stack.empty()) {
                    stack.pop();
                }else if (stack.peek() == '{' && s.charAt(i) == '}' && !stack.empty()) {
                    stack.pop();
                }else if (stack.peek() == '[' && s.charAt(i) == ']' && !stack.empty()) {
                    stack.pop();
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }
        return stack.empty();
    }*/
}
//leetcode submit region end(Prohibit modification and deletion)
