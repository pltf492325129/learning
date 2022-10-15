package adapter.class2;

public class VoltageAdapter extends Voltage220V implements IVoltage5V{

    @Override
    public int ouput5V() {
        int srcV = output220V();
        int dstV = srcV/44;
        return dstV;
    }
}
