package JUC;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class JUC3 {
    public static void main(String[] args) {
        ShareCondition sc = new ShareCondition();
        new Thread(() -> {
            for (int i = 1; i <= 10 ; i++) {
                sc.print5();
            }
        }, "A").start();
        new Thread(() -> {
            for (int i = 1; i <= 10 ; i++) {
                sc.print10();
            }
        }, "B").start();
        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                sc.print15();
            }
        }, "C").start();
    }
}

class ShareCondition {
    private int numver = 1;
    Lock lock = new ReentrantLock();
    private Condition condition1 = lock.newCondition();
    private Condition condition2 = lock.newCondition();
    private Condition condition3 = lock.newCondition();
    public void print5() {
        lock.lock();
        try {
            while (numver != 1) {
                condition1.await();
            }
            for (int i = 1; i <= 5; i++) {
                System.out.println(Thread.currentThread().getName() + "\t" + i);
            }
            numver = 2;
            condition2.signal();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
    public void print10() {
        lock.lock();
        try {
            while (numver != 2) {
                condition2.await();
            }
            for (int i = 1; i <= 10; i++) {
                System.out.println(Thread.currentThread().getName() + "\t" + i);
            }
            numver = 3;
            condition3.signal();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
    public void print15() {
        lock.lock();
        try {
            while (numver != 3) {
                condition3.await();
            }
            for (int i = 1; i <= 15 ; i++) {
                System.out.println(Thread.currentThread().getName() + "\t" + i);
            }
            numver = 1;
            condition1.signal();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}
