package easy;

public class _69MySqrt {
    public int mySqrt(int x) {
        int left = 0;
        int right = x;
        int res = -1;
        while (left <= right) {
            int mid = left + (right-left) / 2;
            /**
             * 注意这里如果不变成长整型的话，就返回原数？？？(long)
             */
            if ((long)mid * mid == x) {
                /**
                 * res放置结果，遍历一遍，最大的值，就是平方根。
                 */
                return mid;
            } else if ((long)mid * mid < x) {
                res = mid;
                left = mid + 1;
            } else if((long) mid * mid > x){
                right = mid - 1;
            }
        }
        return res;

    }

    public static void main(String[] args) {
        _69MySqrt mySqrt = new _69MySqrt();
        int res = mySqrt.mySqrt(2147395599);
        System.out.println(res);

    }
}
