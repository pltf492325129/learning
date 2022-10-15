package jianzhiOffer.queue;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class _59MaxQueue {
    Queue<Integer> q;
    //维护一个单调的双端队列
    Deque<Integer> d;
    public _59MaxQueue() {
        q = new LinkedList<Integer>();
        d = new LinkedList<Integer>();
    }

    public int max_value() {
        if (d.isEmpty()) return -1;
        return d.peekFirst();
    }

    public void push_back(int value) {
        while (!d.isEmpty() && d.peekLast() < value) {
            d.pollLast();
        }
        d.offer(value);
        q.offer(value);
    }

    public int pop_front() {
        if (q.isEmpty()) return -1;
        int ans = q.poll();
        if (ans == max_value()) {
            d.pollFirst();
        }
        return ans;
    }
}
