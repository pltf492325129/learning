package hotPractice;

import java.util.List;

public class PowTest {
    public double myPow(double x, int n) {
        /*if (n >= 0) {
            quickMul(x, n);
        }
        if (n < 0) {
            quickMul(1 / x, -n);
        }*/
        return n >= 0 ? quickMul(x, n) : quickMul(1/x, -n);
    }

    private double quickMul(double x, int n) {
        if (n == 0) {
            return 1;
        }
        double res = quickMul(x, n / 2);
        /**
         * 这里 n % 2 而不是 n / 2
         * */
        return (n % 2 == 0) ? res * res : res * res * x;
/*        if (n / 2 == 1) {
            return myPow(x, n/2)*x;
        }
        double res = myPow(x, n/2);
        return res*res;*/
    }

}
