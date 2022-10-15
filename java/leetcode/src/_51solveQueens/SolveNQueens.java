package _51solveQueens;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SolveNQueens {
    //记得初始化，也就是 new ArraList()
    List<List<String>> res = new ArrayList<List<String>>();

    public List<List<String>> solveNQueens(int n) {
        char[][] board = new char[n][n];
        /**初始化时将所有位置放入 . */
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = '.';
            }
        }

        backtrack(board, 0);
        return res;
    }

    private void backtrack(char[][] board, int row) {
    //   触发结束条件
    //    判断board放置到第几行了
        if (board.length == row) {
            //System.out.println(res);
            res.add(construct(board));
            return;
        }

    // 路径：board中小于row行都放置了皇后
    // 选择列表：在第row行，所有列都是皇后可以的选择列表
    // 结束条件：row超过board的最后一行
        for (int col = 0; col < board.length; col++) {
        //    排除不合法选择
            if (!isValid(board, row, col)) {
                continue;
            }
        //    做选择
            board[row][col] = 'Q';
        //    递归
            backtrack(board, row + 1);
        //    撤销选择
            board[row][col] = '.';
        }
    }

    private boolean isValid(char[][] board, int row, int col) {
    //    检查列中是否有冲突
        int n = board.length;
        for (int i = 0; i < row; i++) {
            if (board[i][col] == 'Q') {
                return false;
            }
        }
    //    检查撇中是否有冲突
        for (int i = row - 1, j= col + 1; i >= 0 && j < n; i--, j++) {
            if (board[i][j] == 'Q') {
                return false;
            }
        }

    //    检查捺中是否有冲突
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (board[i][j] == 'Q') {
                return false;
            }
        }
        return true;
    }
    /**将二维数组转换为一维 字符串列表*/
    private List<String> construct(char[][] board) {
        List<String> res = new LinkedList<String>();
        for (int i = 0; i < board.length; i++) {
            String s = new String(board[i]);
            res.add(s);
        }
        return res;
    }


    public static void main(String[] args) {
        SolveNQueens solveNQueens = new SolveNQueens();
        List<List<String>> lists = solveNQueens.solveNQueens(8);
        for (List<String> list : lists) {
            for (String s : list) {
                System.out.println(s);
            }
            System.out.println("-----------------");
        }
    }
}
