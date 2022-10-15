package jianzhiOffer.day2;

public class _62lastRemaining {
    public int lastRemaining(int n, int m) {

        return f(n, m);
    }

    public int f(int n, int m) {
        if (n == 1)return 0;
        int x = f(n - 1, m);
        //这里x是留下的那个元素,
        //f(n,m) = (m%n + x)%n;
        return (m + x) % n;
    }
}
