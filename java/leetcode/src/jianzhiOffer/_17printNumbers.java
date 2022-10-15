package jianzhiOffer;

public class _17printNumbers {
    StringBuilder res ;
    char[] num, loop = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    int count = 0, start, n;
    int nine = 0;
    public String printNumbers(int n) {
        this.n = n;
        num = new char[n];
        res = new StringBuilder();
        dfs(n);
        res.deleteCharAt(res.length() - 1);    //删除最后多余的逗号
        return res.toString();
    }

    private void dfs(int x) {
        if (x == n) {
            String s = String.valueOf(num).substring(start);
            res.append( String.valueOf(num) + ",");
            return;
        }

        for (char i : loop) {
            //做选择
            if (i == '9') nine++;
            num[x] = i;
            dfs(x + 1);
            //撤销选择
        }
        nine--;
    }

    public static void main(String[] args) {
        for (int i = 0; i < Math.pow(10, 10); i++) {
            System.out.println(i);
        }
    }
}
