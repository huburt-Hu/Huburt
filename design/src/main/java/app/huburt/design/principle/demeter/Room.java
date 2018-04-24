package app.huburt.design.principle.demeter;

/**
 * Created by hubert on 2018/4/24.
 */

public class Room {
    public float area;
    public float price;

    public Room(float area, float price) {
        this.area = area;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Room [area=" + area + ",price=" + price + "]";
    }
}
