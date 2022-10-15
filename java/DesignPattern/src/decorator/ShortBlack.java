package decorator;

public class ShortBlack extends Coffee{
    private float shorBlack;
    public ShortBlack() {
        this.shorBlack = 3.0f;
    }
    @Override
    public float cost() {
        return super.cost() + shorBlack;
    }
}
