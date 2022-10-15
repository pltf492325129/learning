package decorator;

public class Chocolate extends Decorator{

    public Chocolate(Drink drink) {
        super(drink);
        setPrice(3.0f);
        setDescription("加入巧克力哦");
    }
}
