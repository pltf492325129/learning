package JUC;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class JUC2 {
    public static void main(String[] args) {
        AirConditioner air = new AirConditioner();

            new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    try {
                        air.add();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "A").start();
            new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    try {
                        air.sub();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "B").start();
            new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    try {
                        air.add();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "C").start();
            new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    try {
                        air.sub();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "D").start();
    }
}
class AirConditioner {
    private int number = 0;
    Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void add() throws InterruptedException {
        lock.lock();
        try {
            while (number != 0) {
                condition.await();
            }
            System.out.println(Thread.currentThread().getName() + " number是： " + (number++) + "现在number是" + number);
            condition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

    }

    public void sub() throws InterruptedException {
        lock.lock();
        try {
            while (number == 0) {
                condition.await();
            }
            System.out.println(Thread.currentThread().getName() + " number是： " + (number--) + "现在number是" + number);
            condition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}
//public synchronized void add() throws InterruptedException {
//    while (number != 0) {
//        this.wait();
//    }
//    System.out.println(Thread.currentThread().getName()+ " number是： "+ (number++) + "现在number是" + number);
//    this.notifyAll();
//}
//public synchronized void sub() throws InterruptedException {
//    while (number == 0) {
//        this.wait();
//    }
//    System.out.println(Thread.currentThread().getName() + " number是： "+ (number--) + "现在number是" + number);
//    this.notifyAll();
//}

