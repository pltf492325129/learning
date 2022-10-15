package easy;

public class _367IsPerfectSquare {
    public boolean isPerfectSquare(int num) {
        if (num < 2) return true;
        int left = 2, right = num/2;
        long guessSquared;
        /**
         * 注意这里使用了数学尝试，left > 2, right = num / 2;
         * 注意这里都是用了长整型，否则转换数据导致超时
         */
        while (left <= right) {
            int mid = left + (right - left) / 2;
            guessSquared = (long)mid * mid;
            if (guessSquared == num) {
                return true;
            } else if (guessSquared < num) {
                left = mid + 1;
            }else {
                right = right - 1;
            }
        }
        return false;
    }
    public static void main(String[] args) {
        _367IsPerfectSquare isPerfectSquare = new _367IsPerfectSquare();
        boolean res = isPerfectSquare.isPerfectSquare(808201);
        boolean res2 = isPerfectSquare.isPerfectSquare(2000105819);

        System.out.println(res);
        System.out.println(res2);
    }
}
