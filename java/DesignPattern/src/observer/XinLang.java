package observer;

public class XinLang implements IObserver{

    @Override
    public void update(Float weather, Float humidity, Float pressure) {
        this.weather = weather;
        this.humidity = humidity;
        this.pressure = pressure;
        System.out.println(this);
    }

    public Float weather;
    public Float humidity;
    public Float pressure;


    @Override
    public String toString() {
        return "XinLang{" +
                "weather=" + weather +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                '}';
    }
}
