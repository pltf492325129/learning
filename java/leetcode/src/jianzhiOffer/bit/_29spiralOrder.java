package jianzhiOffer.bit;

public class _29spiralOrder {
    public int[] spiralOrder(int[][] matrix) {
        if (matrix.length == 0) return new int[0];
        int l = 0, r = matrix[0].length - 1, t = 0, b = matrix.length - 1, x =0;
        int[] res = new int[(r+1)*(b+1)];

        while (true) {
            for (int i = l; i <= r; i++) res[x++] = matrix[t][i];
            if (++t > b)  break;
            //注意这里是--r
            for (int j = t; j <= b; j++) res[x++] = matrix[j][r];
            if (l > --r) break;
            for (int i = r; i >= l; i--) res[x++] = matrix[b][i];
            //注意这里是--b
            if (t > --b) break;
            for (int i = b; i >= t; i--) res[x++] = matrix[i][l];
            if (++l > r) break;

        }

        return res;
    }
}
