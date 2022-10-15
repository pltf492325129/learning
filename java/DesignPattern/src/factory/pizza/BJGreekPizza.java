package factory.pizza;

public class BJGreekPizza extends Pizza{
    @Override
    public void prepare() {
        setName("Greek");
        System.out.println("BJGreekPizza preparing");
    }
}
