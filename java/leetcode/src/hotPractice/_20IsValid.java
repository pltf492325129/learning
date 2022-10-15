package hotPractice;

import java.util.Deque;
import java.util.LinkedList;

public class _20IsValid {
    public boolean isValid(String s) {
        Deque<Character> stack = new LinkedList<>();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(' || s.charAt(i) == '[' || s.charAt(i) == '{') {
                stack.add(s.charAt(i));
            }else {
                if (s.charAt(i) == ')') {
                    if (stack.poll() == '(') continue;
                }else if (s.charAt(i) == '}'){
                    if (stack.poll() == '{') continue;
                } else if (s.charAt(i) == ']') {
                    if (stack.poll() == '[') continue;
                }
                return false;
            }
        }
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        _20IsValid isValid = new _20IsValid();
        boolean valid = isValid.isValid("()[]{}");
        System.out.println(valid);
    }
}
