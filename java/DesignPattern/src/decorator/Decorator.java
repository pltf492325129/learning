package decorator;

public class Decorator extends Drink{
    private Drink drink;
    public Decorator(Drink drink) {
        this.drink = drink;
    }

    @Override
    public float cost() {
        return super.getPrice() + drink.cost();
    }

    @Override
    public String getDescription() {
        return description + " "+ getPrice() + " && "
                + drink.getDescription() + drink.getPrice();
    }
}
