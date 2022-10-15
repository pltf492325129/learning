package decorator;

public class Milk extends Decorator{

    public Milk(Drink drink) {
        super(drink);
        setPrice(2.0f);
        setDescription("牛奶 ");
    }
}
