package root.models;

public enum VerticalDirection {
    UP,
    DOWN;

    public VerticalDirection opposite() {
        return this.equals(VerticalDirection.UP) ? DOWN : UP;
    }
}
