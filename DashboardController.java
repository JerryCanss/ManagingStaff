package acss.controller;

import acss.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private TabPane tabPane;

    // User management
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TextField userUsernameField;
    @FXML private PasswordField userPasswordField;
    @FXML private ComboBox<String> userRoleComboBox;
    @FXML private Label userMsgLabel;

    // Car management
    @FXML private TableView<Car> carTable;
    @FXML private TableColumn<Car, String> colCarId;
    @FXML private TableColumn<Car, String> colCarModel;
    @FXML private TableColumn<Car, String> colCarBrand;
    @FXML private TableColumn<Car, String> colCarPrice;
    @FXML private TableColumn<Car, String> colCarAvailable;
    @FXML private TextField carIdField;
    @FXML private TextField carModelField;
    @FXML private TextField carBrandField;
    @FXML private TextField carPriceField;
    @FXML private ComboBox<String> carAvailableComboBox;
    @FXML private Label carMsgLabel;

    private User currentUser;
    private ObservableList<User> userList;
    private ObservableList<Car> carList;

    public void setUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getUsername() + "!");
        roleLabel.setText("Role: " + user.getRole().toString().replace("_", " "));

        if (user.getRole() != UserRole.MANAGING_STAFF) {
            tabPane.getTabs().removeIf(tab -> !tab.getText().equals("Analysis & Reports"));
        } else {
            initUserTab();
            initCarTab();
        }
    }

    // --- User Management ---
    private void initUserTab() {
        userRoleComboBox.setItems(FXCollections.observableArrayList("MANAGING_STAFF", "SALESMAN", "CUSTOMER"));
        userRoleComboBox.getSelectionModel().select("SALESMAN");

        colUsername.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUsername()));
        colRole.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRole().toString()));

        userList = FXCollections.observableArrayList(UserStore.getAllUsers());
        userTable.setItems(userList);

        userTable.setOnMouseClicked((MouseEvent event) -> {
            User selected = userTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                userUsernameField.setText(selected.getUsername());
                userPasswordField.setText(selected.getPassword());
                userRoleComboBox.setValue(selected.getRole().toString());
            }
        });
    }

    @FXML
    private void handleAddUser() {
        String username = userUsernameField.getText();
        String password = userPasswordField.getText();
        String roleStr = userRoleComboBox.getValue();
        if (username.isEmpty() || password.isEmpty() || roleStr == null) {
            userMsgLabel.setText("Fill all fields.");
            return;
        }
        if (UserStore.usernameExists(username)) {
            userMsgLabel.setText("Username exists.");
            return;
        }
        UserRole role = UserRole.valueOf(roleStr);
        User user = new User(username, password, role);
        UserStore.addUser(user);
        userList.add(user);
        userMsgLabel.setText("User added.");
        clearUserFields();
    }

    @FXML
    private void handleUpdateUser() {
        String username = userUsernameField.getText();
        String password = userPasswordField.getText();
        String roleStr = userRoleComboBox.getValue();
        if (username.isEmpty() || password.isEmpty() || roleStr == null) {
            userMsgLabel.setText("Fill all fields.");
            return;
        }
        User user = UserStore.findUserByUsername(username);
        if (user == null) {
            userMsgLabel.setText("User not found.");
            return;
        }
        userList.remove(user);
        UserStore.removeUser(username);
        UserRole role = UserRole.valueOf(roleStr);
        User updated = new User(username, password, role);
        UserStore.addUser(updated);
        userList.add(updated);
        userMsgLabel.setText("User updated.");
        clearUserFields();
    }

    @FXML
    private void handleDeleteUser() {
        String username = userUsernameField.getText();
        if (username.isEmpty()) {
            userMsgLabel.setText("Enter username.");
            return;
        }
        User user = UserStore.findUserByUsername(username);
        if (user == null) {
            userMsgLabel.setText("User not found.");
            return;
        }
        userList.remove(user);
        UserStore.removeUser(username);
        userMsgLabel.setText("User deleted.");
        clearUserFields();
    }

    @FXML
    private void handleSearchUser() {
        String username = userUsernameField.getText();
        if (username.isEmpty()) {
            userMsgLabel.setText("Enter username.");
            return;
        }
        User user = UserStore.findUserByUsername(username);
        if (user == null) {
            userMsgLabel.setText("User not found.");
            return;
        }
        userTable.getSelectionModel().select(user);
        userUsernameField.setText(user.getUsername());
        userPasswordField.setText(user.getPassword());
        userRoleComboBox.setValue(user.getRole().toString());
        userMsgLabel.setText("User found.");
    }

    private void clearUserFields() {
        userUsernameField.clear();
        userPasswordField.clear();
        userRoleComboBox.getSelectionModel().select("SALESMAN");
    }

    // --- Car Management ---
    private void initCarTab() {
        carAvailableComboBox.setItems(FXCollections.observableArrayList("true", "false"));
        carAvailableComboBox.getSelectionModel().select("true");

        colCarId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));
        colCarModel.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getModel()));
        colCarBrand.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getBrand()));
        colCarPrice.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getPrice())));
        colCarAvailable.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().isAvailable())));

        carList = FXCollections.observableArrayList(CarStore.getCars());
        carTable.setItems(carList);

        carTable.setOnMouseClicked((MouseEvent event) -> {
            Car selected = carTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                carIdField.setText(selected.getId());
                carModelField.setText(selected.getModel());
                carBrandField.setText(selected.getBrand());
                carPriceField.setText(String.valueOf(selected.getPrice()));
                carAvailableComboBox.setValue(String.valueOf(selected.isAvailable()));
            }
        });
    }

    @FXML
    private void handleAddCar() {
        String id = carIdField.getText();
        String model = carModelField.getText();
        String brand = carBrandField.getText();
        String priceStr = carPriceField.getText();
        String availableStr = carAvailableComboBox.getValue();
        if (id.isEmpty() || model.isEmpty() || brand.isEmpty() || priceStr.isEmpty() || availableStr == null) {
            carMsgLabel.setText("Fill all fields.");
            return;
        }
        if (CarStore.findCar(id) != null) {
            carMsgLabel.setText("Car ID exists.");
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            carMsgLabel.setText("Invalid price.");
            return;
        }
        boolean available = Boolean.parseBoolean(availableStr);
        Car car = new Car(id, model, brand, price, available);
        CarStore.addCar(car);
        carList.add(car);
        carMsgLabel.setText("Car added.");
        clearCarFields();
    }

    @FXML
    private void handleUpdateCar() {
        String id = carIdField.getText();
        String model = carModelField.getText();
        String brand = carBrandField.getText();
        String priceStr = carPriceField.getText();
        String availableStr = carAvailableComboBox.getValue();
        if (id.isEmpty() || model.isEmpty() || brand.isEmpty() || priceStr.isEmpty() || availableStr == null) {
            carMsgLabel.setText("Fill all fields.");
            return;
        }
        Car car = CarStore.findCar(id);
        if (car == null) {
            carMsgLabel.setText("Car not found.");
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            carMsgLabel.setText("Invalid price.");
            return;
        }
        boolean available = Boolean.parseBoolean(availableStr);
        car.setModel(model);
        car.setBrand(brand);
        car.setPrice(price);
        car.setAvailable(available);
        CarStore.saveCars();
        carTable.refresh();
        carMsgLabel.setText("Car updated.");
        clearCarFields();
    }

    @FXML
    private void handleDeleteCar() {
        String id = carIdField.getText();
        if (id.isEmpty()) {
            carMsgLabel.setText("Enter car ID.");
            return;
        }
        Car car = CarStore.findCar(id);
        if (car == null) {
            carMsgLabel.setText("Car not found.");
            return;
        }
        carList.remove(car);
        CarStore.removeCar(id);
        carMsgLabel.setText("Car deleted.");
        clearCarFields();
    }

    @FXML
    private void handleSearchCar() {
        String id = carIdField.getText();
        if (id.isEmpty()) {
            carMsgLabel.setText("Enter car ID.");
            return;
        }
        Car car = CarStore.findCar(id);
        if (car == null) {
            carMsgLabel.setText("Car not found.");
            return;
        }
        carTable.getSelectionModel().select(car);
        carIdField.setText(car.getId());
        carModelField.setText(car.getModel());
        carBrandField.setText(car.getBrand());
        carPriceField.setText(String.valueOf(car.getPrice()));
        carAvailableComboBox.setValue(String.valueOf(car.isAvailable()));
        carMsgLabel.setText("Car found.");
    }

    private void clearCarFields() {
        carIdField.clear();
        carModelField.clear();
        carBrandField.clear();
        carPriceField.clear();
        carAvailableComboBox.getSelectionModel().select("true");
    }
} 