package factory.order;

import factory.pizza.BJCheesePizza;
import factory.pizza.BJGreekPizza;
import factory.pizza.Pizza;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OrderPizza {
    AbsFactory factory;

    public OrderPizza(AbsFactory factory) {
        setFactory(factory);
    }

    public void setFactory(AbsFactory factory) {
        String orderType = "";
        Pizza pizza = null;
        this.factory = factory;
        do {
            orderType = getOrderType();
            pizza = factory.createPizza(orderType);
            if (pizza != null) {
                pizza.prepare();
                pizza.bake();
                pizza.cut();
                pizza.box();
            } else {
                System.out.println("订购失败");
                break;
            }
        } while (true);
    }

    private String getOrderType(){
        System.out.println("请输入你想要的Pizza：");
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String input = br.readLine();
            return input;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
