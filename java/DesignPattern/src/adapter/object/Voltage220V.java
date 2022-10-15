package adapter.object;

public class Voltage220V {
    public int output220V() {
        int srcV = 220;

        System.out.println("这是" + srcV + "电压");
        return srcV;
    }
}
