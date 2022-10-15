package factory.pizza;

public class LDCheesePizza extends Pizza{
    @Override
    public void prepare() {
        System.out.println("LDpizza preparing");
    }
}
