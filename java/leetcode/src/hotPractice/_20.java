package hotPractice;

import java.util.ArrayList;
import java.util.List;

public class _20 {
    private List<String> result;
    public static void main(String[] args) {
        _20 sol = new _20();
        System.out.println(sol.generateParenthesis(3));
    }

    public  List<String> generateParenthesis(int n) {
        result = new ArrayList<String>();
        _helper(0,0, n, "");
        return result;
    }

    private void _helper(int left,int right, int maxLevel, String s) {
        //    1 recursion terminator
        if (left == maxLevel && right == maxLevel) {
            System.out.println(s);
            result.add(s);
            return ;
        }
        //    2 process logic in current level: left right
        //    3 drill down
        if (left < maxLevel) _helper(left + 1,right, maxLevel, s + "(");
        if (right < left) _helper(left,right + 1, maxLevel, s + ")" );
        //    4 restor curren status
    }
}
