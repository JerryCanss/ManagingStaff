package acss.model;

import java.io.Serializable;

public class Car implements Serializable {
    private String id;
    private String model;
    private String brand;
    private String manufactured;
    private double price;
    private boolean available;

    public Car(String id, String brand, String model, String manufactured, double price, boolean available) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.manufactured = manufactured;
        this.price = price;
        this.available = available;
    }

    public String getId() { return id; }
    public String getModel() { return model; }
    public String getBrand() { return brand; }
    public String getManufactured() { return manufactured; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setModel(String model) { this.model = model; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setManufactured(String manufactured) { this.manufactured = manufactured; }
    public void setPrice(double price) { this.price = price; }
} 