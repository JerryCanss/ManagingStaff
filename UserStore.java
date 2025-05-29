package acss.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserStore {
    private static final String FILE_NAME = "users.dat";
    private static List<User> users = new ArrayList<>();

    static {
        loadUsers();
    }

    public static void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    // Login: match by full name or email, and password
    public static User findUser(String usernameOrEmail, String password) {
        for (User user : users) {
            if ((user.getFullName().equalsIgnoreCase(usernameOrEmail) || user.getEmail().equalsIgnoreCase(usernameOrEmail))
                && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public static boolean usernameExists(String staffId) {
        for (User user : users) {
            if (user.getStaffId().equalsIgnoreCase(staffId)) return true;
        }
        return false;
    }

    public static boolean emailExists(String email) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) return true;
        }
        return false;
    }

    public static List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public static User findUserByStaffId(String staffId) {
        for (User user : users) {
            if (user.getStaffId().equalsIgnoreCase(staffId)) return user;
        }
        return null;
    }

    // Find by full name (for dashboard search, not unique)
    public static User findUserByFullName(String fullName) {
        for (User user : users) {
            if (user.getFullName().equalsIgnoreCase(fullName)) return user;
        }
        return null;
    }

    public static void removeUser(String staffId) {
        users.removeIf(user -> user.getStaffId().equalsIgnoreCase(staffId));
        saveUsers();
    }

    private static void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            users = (List<User>) ois.readObject();
        } catch (Exception e) {
            users = new ArrayList<>();
        }
    }
} 