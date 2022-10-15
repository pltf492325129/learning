package decorator;

public class CoffeeStore {
    public static void main(String[] args) {
        Drink order = new Decaf();
        System.out.print("描述:" + order.getDescription() + " ");
        System.out.println("费用1 = " + order.cost());


        order = new Milk(order);
        System.out.println("order 加入一份牛奶 费用 =" + order.cost());
        System.out.println("order 加入一份牛奶 描述 = " + order.getDescription());

        order = new Milk(order);
        System.out.println("order 加入一份牛奶 费用 =" + order.cost());
        System.out.println("order 加入一份牛奶 描述 = " + order.getDescription());

        order = new Chocolate(order);
        System.out.println("order加入一份巧克力后费用 = " + order.cost());
    }
}
