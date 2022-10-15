package observer;

import java.util.ArrayList;
import java.util.Arrays;

public class WeatherData implements ISubject{
    public ArrayList<IObserver> observers;
    public Float weather;
    public Float humidity;
    public Float pressure;


    public Float getWeather() {
        return weather;
    }

    public void setWeather(Float humidity) {
        this.humidity = humidity;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    public Float getPressure() {
        return pressure;
    }

    public void setPressure(Float pressure) {
        this.pressure = pressure;
    }
    public WeatherData(Float weather, Float humidity, Float pressure) {
        this.observers = new ArrayList<IObserver>();
        this.weather = weather;
        this.humidity = humidity;
        this.pressure = pressure;
    }
    public void dataChange(Float weather, Float humidity, Float pressure) {
        setWeather(weather);
        setHumidity(humidity);
        setPressure(pressure);
        notifyObservers();
    }

    @Override
    public void registerObserver(IObserver observer) {
        //增加通知者到数组里
        observers.add(observer);
    }

    @Override
    public void remove(IObserver observer) {
        if (observers.contains(observer)) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers() {
        //循环通知每一个观察者，调用update函数
        for (IObserver observer: observers) {
            observer.update(this.weather, this.humidity, this.pressure);
        }
    }

}
