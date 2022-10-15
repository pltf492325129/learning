package adapter.class2;

public class Client {
    public static void main(String[] args) {
        Phone phone = new Phone();
        VoltageAdapter voltageAdapter = new VoltageAdapter();

        phone.charging(voltageAdapter);


    }
}
