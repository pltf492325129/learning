package singleton;

public class SingletonTest02 implements Runnable{
    public static void main(String[] args) {
        Singleton2 singleton = new Singleton2();
        Singleton2 instance1 = singleton.getInstance();
        Singleton2 instance2 = singleton.getInstance();
        System.out.println(instance1 == instance2);
        System.out.println("instance.hashcode = " + instance1.hashCode());
        System.out.println("instance2.hashcode = " + instance2.hashCode());
        //多线程
    }
    private String threadName;
    public Thread t;

    SingletonTest02(String name) {
        threadName = name;
    }

    @Override
    public void run() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start () {
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}

class Singleton2 {
    private static Singleton2 singleton;

    public Singleton2() {

    }

    public Singleton2 getInstance() {
        synchronized (this) {
            if (singleton == null) {
                singleton = new Singleton2();
            }
        }
        return singleton;
    }


}