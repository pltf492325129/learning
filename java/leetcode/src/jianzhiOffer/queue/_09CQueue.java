package jianzhiOffer.queue;

import java.util.LinkedList;

public class _09CQueue {
    LinkedList<Integer> A,B;
    public  _09CQueue() {
        A = new LinkedList<Integer>();
        B = new LinkedList<Integer>();
    }

    public void appendTail(int value) {
        A.addLast(value);
    }

    public int deleteHead() {
        //保证B中是顺序的，最上边是最先出链表的。
        if (!B.isEmpty()) return B.removeLast();
        if (A.isEmpty()) return -1;
        while (!A.isEmpty()) {
            B.add(A.removeLast());
        }
        return B.removeLast();
    }
}
