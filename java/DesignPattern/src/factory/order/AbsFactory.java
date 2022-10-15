package factory.order;

import factory.pizza.Pizza;

public interface AbsFactory {
    public Pizza createPizza(String orderType);
}
