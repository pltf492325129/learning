package observer;


public interface ISubject {
    public void registerObserver(IObserver observer);

    public void remove(IObserver observer);

    public void notifyObservers();
}
