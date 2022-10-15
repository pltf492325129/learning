package factory.order;

import factory.pizza.BJCheesePizza;
import factory.pizza.BJGreekPizza;
import factory.pizza.Pizza;

public class BJFactory implements AbsFactory{

    @Override
    public Pizza createPizza(String orderType) {
        String orederType = orderType;
        Pizza pizza = null;
        if (orederType.equals("Cheese")) {
            pizza = new BJCheesePizza();
        } else if (orederType.equals("Greek")) {
            pizza = new BJGreekPizza();
        }
        return pizza;
    }

}
