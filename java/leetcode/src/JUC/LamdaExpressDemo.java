package JUC;

@FunctionalInterface
interface Foo {
    //public void sayHello(String num);

    public void add(int x, int y);
    public default int mul(int x, int y) {
        return x*y;
    }
    public default int mul2(int x, int y) {
        return x*y;
    }

    public static int div(int x, int y) {
        return x/y;
    }
}
public class LamdaExpressDemo {
    public static void main(String[] args) {
        //Foo foo = new Foo() {
        //    @Override
        //    public void sayHello() {
        //        System.out.println("sayHello....");
        //    }
        //};
        Foo foo = (int x, int y) -> {
            System.out.println("sayHello...."+x + y);
        };
        foo.add(2,3);
        System.out.println(foo.mul(3, 4));;
        System.out.println(Foo.div(10, 2));
    }
}
