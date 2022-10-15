package factory.order;

public class PizzaStore {
    public static void main(String[] args) {
        //OrderPizza orderPizza = new OrderPizza();
        //orderPizza.getPizza();
        new OrderPizza(new BJFactory());

    }
}
