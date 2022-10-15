package factory.pizza;

public class BJCheesePizza extends Pizza{
    @Override
    public void prepare() {
        setName("北京的奶酪pizza");
        System.out.println(super.name + "BJpizza preparing");
    }
}
