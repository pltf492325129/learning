package adapter.object;

import adapter.object.Phone;
import adapter.object.VoltageAdapter;

public class Client {
    public static void main(String[] args) {
        Phone phone = new Phone();

        Voltage220V voltage220V = new Voltage220V();
        VoltageAdapter voltageAdapter = new VoltageAdapter();

        phone.charging(voltageAdapter);


    }
}
