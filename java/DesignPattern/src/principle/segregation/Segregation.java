package principle.segregation;

public class Segregation {
    public static void main(String[] args) {
        A a = new A();
        B b = new B();
        a.depend1(b);
        a.depend2(b);
        a.depend3(b);

        D d = new D();
        C c = new C();
        c.depend1(d);
        c.depend4(d);
        c.depend5(d);

    }
}

interface Interface1 {
    void interface1();
}

interface Interface2 {
    void interface2();
    void interface3();
}

interface Interface3 {
    void interface4();
    void interface5();
}

class A {
    void depend1(Interface1 i) {
        i.interface1();
    }
    void depend2(Interface2 i) {
        i.interface2();
    }
    void depend3(Interface2 i) {
        i.interface3();
    }
}

class B implements Interface1, Interface2{

    @Override
    public void interface1() {
        System.out.println("B实现interface1");
    }

    @Override
    public void interface2() {
        System.out.println("B实现interface2");
    }

    @Override
    public void interface3() {
        System.out.println("B实现interface3");
    }
}

class C {
    static void depend1(Interface1 i) {
        i.interface1();
    }
    static void depend4(Interface3 i) {
        i.interface4();
    }
    static void depend5(Interface3 i) {
        i.interface5();
    }
}

class D implements Interface1,Interface3{

    @Override
    public void interface1() {
        System.out.println("D实现interface1");
    }

    @Override
    public void interface4() {
        System.out.println("D实现了interface4");
    }

    @Override
    public void interface5() {
        System.out.println("D实现了interface5");
    }
}

