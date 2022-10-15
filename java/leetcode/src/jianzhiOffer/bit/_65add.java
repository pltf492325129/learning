package jianzhiOffer.bit;

public class _65add {
    public int add(int a, int b) {
        int sum = a ^ b;//记得左移一位
        int carry = (a & b) << 1;
        while (carry != 0) {
            int oralSum = sum;
            sum = sum ^ carry;
            carry = (oralSum & carry) << 1;
        }
        return sum;
    }

    public static void main(String[] args) {
        int  c = 1 & 1;
        System.out.println(c);
    }
}
