//n 皇后问题 研究的是如何将 n 个皇后放置在 n×n 的棋盘上，并且使皇后彼此之间不能相互攻击。 
//
// 给你一个整数 n ，返回所有不同的 n 皇后问题 的解决方案。 
//
// 
// 
// 每一种解法包含一个不同的 n 皇后问题 的棋子放置方案，该方案中 'Q' 和 '.' 分别代表了皇后和空位。 
//
// 
//
// 示例 1： 
//
// 
//输入：n = 4
//输出：[[".Q..","...Q","Q...","..Q."],["..Q.","Q...","...Q",".Q.."]]
//解释：如上图所示，4 皇后问题存在两个不同的解法。
// 
//
// 示例 2： 
//
// 
//输入：n = 1
//输出：[["Q"]]
// 
//
// 
//
// 提示： 
//
// 
// 1 <= n <= 9 
// 皇后彼此不能相互攻击，也就是说：任何两个皇后都不能处于同一条横行、纵行或斜线上。 
// 
// 
// 
// Related Topics 回溯算法 
// 👍 914 👎 0

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    List<List<String>> res = new ArrayList<List<String>>();

    public List<List<String>> solveNQueens(int n) {
        char[][] board = new char[n][n];
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
    
    private List<String> construct(char[][] board) {
        List<String> res = new LinkedList<String>();
        for (int i = 0; i < board.length; i++) {
            String s = new String(board[i]);
            res.add(s);
        }
        return res;
    }

}
//leetcode submit region end(Prohibit modification and deletion)
