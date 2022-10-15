package factory.pizza;

public abstract class Pizza {
    public String name;
    public abstract void prepare();

    public void bake() {
        System.out.println(name + "bakeing~");
    }
    public void cut() {
        System.out.println(name + "cutting~");
    }
    public void box() {
        System.out.println(name + "boxing~");
    }

    public void setName(String name) {
        this.name = name;
    }
}
