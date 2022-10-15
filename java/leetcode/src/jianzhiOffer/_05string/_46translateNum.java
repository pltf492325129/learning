package jianzhiOffer._05string;


public class _46translateNum {

    public int translateNum(int num) {
        //dp[i] 表示i个字符组成的情况
        //dp[i] = dp[i-1] + dp[i-2]
        //初始化 dp[2] = dp[1] + dp[0]; 因为dp[1] = 1 所以 dp[0] = 1;
        String s = String.valueOf(num);
        int a =1, b = 1;

        for (int i = 2; i < s.length(); i++) {
            String tmp = s.substring(i - 2, i);
            int c =tmp.compareTo("10") >= 0 && tmp.compareTo("25") <= 0 ? a + b : a;
            b = a;
            a = c;
        }
        return a;
    }


    public void heap_sort(int[] tree, int n) {
        build_heap(tree, n);
        for (int i = n - 1; i >= 0; i--) {
            swap(tree, i, 0);
            heapify(tree, i, 0);
        }
    }

    private void build_heap(int[] tree, int n) {
        int child_max = n - 1;
        int lc = (child_max - 1) / 2;
        for (int i = lc; i >= 0; i--) {
            heapify(tree, n, i);
        }
    }

    private void heapify(int[] tree, int n, int i) {
        if (i >= n) return;
        int max = i;
        int c1 = 2 * i + 1;
        int c2 = 2 * i + 2;
        if ( c1 < n && tree[c1] > tree[max]) max = c1;
        if (c2 < n && tree[c2] > tree[max]) max = c2;
        if (max != i){
            //注意这里swap（tree, max , i） 是i
            swap(tree, max, i);
            heapify(tree, n, max);
        }
    }

    private void swap(int[] tree, int i1, int i2) {
        int temp = tree[i2];
        tree[i2] = tree[i1];
        tree[i1] = temp;
    }

    public static void main(String[] args) {
        int[] tree = {10, 4, 5, 8, 2, 6, 1};
        _46translateNum translateNum = new _46translateNum();
        translateNum.heap_sort(tree, 7);
        for (int i : tree) {
            System.out.println(i);

        }
    }
}
