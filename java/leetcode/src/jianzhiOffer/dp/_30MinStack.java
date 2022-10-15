package jianzhiOffer.dp;

import java.util.LinkedList;
import java.util.Stack;

public class _30MinStack {
    Stack<Integer> A;
    Stack<Integer> B;
    public _30MinStack() {
        A = new Stack<>();
        B = new Stack<>();
    }

    public void push(int x) {
        A.add(x);
        //注意这里考虑B为空的时候
        if (B.isEmpty() || x <= min()) {
            B.add(x);
        }
    }

    public void pop() {
        int ans = A.pop();
        if (ans == B.peek()) {
            B.pop();
        }
    }

    public int top() {
        return A.peek();
    }

    public int min() {
        return B.peek();
    }
}
