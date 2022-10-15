package hotPractice;

import java.util.Arrays;
import java.util.Scanner;

public class InputOutput {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.next();
        String[] s1 = s.split(" ");
        System.out.println(s1);
        int totalLen = 0;
        for (int i = 0; i < s1.length; i++) {
            totalLen += s1[i].length();
        }
        System.out.println(totalLen/s1.length);

    }
    public static void main5(String[] args) {
        Scanner sc = new  Scanner(System.in);
        int n = sc.nextInt();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            String next = sc.next();
            sb.append(next);
        }
        String s = sb.toString();
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        for (char aChar : chars) {
            System.out.print(aChar);
        }
        System.out.println();
        String string = chars.toString();
        System.out.println(string);
    }

    public static void main4(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        for (int i = 0; i < n; i++) {
            int a = sc.nextInt();
            int b = sc.nextInt();
            if (i > 0) {
                System.out.println("Case" + (i+1) + ": " + (a + b) + "\n");
            }
            System.out.println("Case" + (i+1) + ": " + (a + b));
        }
    }

    public static void main3(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        for (int i = 0; i < n; i++) {
            int a = sc.nextInt();
            int b = sc.nextInt();
            System.out.println("Case" + (i+1) + ": " + (a + b) + "\n");
        }

    }
    public static void main2(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        for (int i = 0; i < n; i++) {
            int a = sc.nextInt();
            int b = sc.nextInt();
            System.out.println(a + b);
        }
    }
}
