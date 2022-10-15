package adapter.interface2;

public class Test {
    public static void main(String[] args) {
        AbsAbstract absAbstract = new AbsAbstract() {
            @Override
            public void m1() {
                System.out.println("重写改写m1方法");
            }
        };
        absAbstract.m1();
        absAbstract.m2();
        absAbstract.m3();

    }

}
