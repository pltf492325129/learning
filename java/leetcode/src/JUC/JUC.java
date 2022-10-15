package JUC;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class JUC {
    private static int ticket;
    public static void main(String[] args) {
        Ticket ticket = new Ticket();

        new Thread(() -> { for (int i = 0; i <= 50; i++) ticket.sale(); }, "A").start();
        new Thread(() -> { for (int i = 0; i <= 50; i++) ticket.sale(); }, "B").start();
        new Thread(() -> { for (int i = 0; i <= 50; i++) ticket.sale(); }, "C").start();


    }
}

class Ticket {
    private int number = 1000;
    Lock lock = new ReentrantLock();
    public  void sale() {
        lock.lock();
        try {
            //操作
            if (number > 0){
                System.out.println(Thread.currentThread().getName() + "\t卖出第:" + (number--) + "张票，还剩第\t" + number);
                number--;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}
