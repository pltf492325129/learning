package hotPractice;

import java.util.Scanner;

public class RandomTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        char[][] board = new char[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = '.';
            }
        }
        System.out.println("board的长度是："+board.length);
        board[1][0] = 'Q';
        board[2][3] = 'Q';
        board[6][4] = 'Q';
        board[7][8] = 'Q';

        for (int i = 0; i < board.length; i++) {
            System.out.println(board[i]);
            System.out.println("--------------");
        }
        System.out.println();
    }
}

class Test2 {
    char[][] board = new char[5][5];
}
