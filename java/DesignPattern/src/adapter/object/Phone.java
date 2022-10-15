package adapter.object;

import adapter.object.IVoltage5V;

public class Phone {

    public void charging(IVoltage5V iVoltage5V) {
        if (iVoltage5V.ouput5V() == 5) {
            System.out.println("此时可以使用5v电压充电");
        }else {
            System.out.println("此时不能用非5v电压充电");
        }

    }
}
