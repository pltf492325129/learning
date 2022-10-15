package jianzhiOffer.day2;

public class _16myPow {

    public double myPow(double x, int n) {
        return pow(x, n);
    }
    double pow(double x, int n) {
        if (n == 0) return 1;
        if (n < 0) {
            n = -n;
            x = 1/x;
        }
        return (n % 2 == 0) ? pow(x * x, n / 2) :x * pow(x * x, n / 2) ;
    }

    public double myPow2(double x, int n) {
        return n >= 0 ? quickMul(x, n) : 1.0 / quickMul(x, -n);
    }

    private double quickMul(double x, int n) {
        if (n == 0) {
            return 1.0;
        }
        double res = quickMul(x, n / 2);
        /**
         * 这里 n % 2 而不是 n / 2
         * */
        return (n % 2 == 0) ? res * res : res * res * x;
    }
}
