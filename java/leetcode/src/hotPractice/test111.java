package hotPractice;

import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class test111 {
    public static void main(String[] args){
        ReentrantLock lock = new ReentrantLock();
        HashMap<Object, Object> hashMap = new HashMap<>();
        Object o = new Object();
        synchronized (o) {

        }
    }

}
