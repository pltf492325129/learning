package observer;

public class Client {
    public static void main(String[] args) {
        WeatherData weatherData = new WeatherData(25.6f,60.9f,170.1f);

        XinLang xinLang = new XinLang();
        Baidu baidu = new Baidu();
        weatherData.registerObserver(xinLang);
        weatherData.registerObserver(baidu);

        weatherData.notifyObservers();
        //xinLang.toString();
        //System.out.println(xinLang);
        //System.out.println(baidu);
        weatherData.remove(baidu);

        System.out.println("=======天气改变了======");
        weatherData.dataChange(35.3f, 70.0f, 16.3f);
        //System.out.println(xinLang);
        //System.out.println(baidu);

    }
}
