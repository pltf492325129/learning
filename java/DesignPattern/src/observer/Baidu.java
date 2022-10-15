package observer;

public class Baidu implements IObserver{
    public Float weather;
    public Float humidity;
    public Float pressure;

    @Override
    public void update(Float weather, Float humidity, Float pressure) {
        this.weather = weather;
        this.humidity = humidity;
        this.pressure = pressure;
        System.out.println(this);
    }



    @Override
    public String toString() {
        return "Baidu{" +
                "weather=" + weather +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                '}';
    }
}
