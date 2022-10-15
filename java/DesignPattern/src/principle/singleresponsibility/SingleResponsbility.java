package principle.singleresponsibility;

public class SingleResponsbility {
    public static void main(String[] args) {
        Vehicle vehicle = new Vehicle();
        vehicle.runRoad("汽车");
        vehicle.run("摩托车");
        vehicle.runAir("飞机");

    }
}

class Vehicle {
    public void run(String vehicle) {
        System.out.println(vehicle + "在公路运行");
    }
    public void runRoad(String vehicle) {
        System.out.println(vehicle + "在路上跑");
    }
    public void runAir(String vehicle) {
        System.out.println(vehicle + "在天上飞");
    }
}
