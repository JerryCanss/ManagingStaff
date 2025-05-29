package acss.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CarStore {
    private static final String FILE_NAME = "cars.dat";
    private static List<Car> cars = new ArrayList<>();

    static {
        loadCars();
    }

    public static void addCar(Car car) {
        cars.add(car);
        saveCars();
    }

    public static void removeCar(String id) {
        cars.removeIf(car -> car.getId().equals(id));
        saveCars();
    }

    public static List<Car> getCars() {
        return new ArrayList<>(cars);
    }

    public static Car findCar(String id) {
        for (Car car : cars) {
            if (car.getId().equals(id)) return car;
        }
        return null;
    }

    public static void saveCars() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(cars);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadCars() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            cars = (List<Car>) ois.readObject();
        } catch (Exception e) {
            cars = new ArrayList<>();
        }
    }
} 