package adapter.object;

public class VoltageAdapter implements IVoltage5V{
    public Voltage220V voltage220V;

    public VoltageAdapter() {
        this.voltage220V = new Voltage220V();
    }

    @Override
    public int ouput5V() {
        int srcV = voltage220V.output220V();
        int dstV = srcV/44;
        return dstV;
    }
}
