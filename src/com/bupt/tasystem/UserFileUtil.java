package com.bupt.tasystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserFileUtil {

    private static final String FILE_PATH = "users.json";

    // Save all users to JSON file
    public static void saveUsers(List<User> users) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load all users from JSON file
    public static List<User> loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
            List<User> users = gson.fromJson(reader, userListType);
            return users != null ? users : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Find user by username
    public static User findUserByUsername(String username) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    // Add a new user
    public static void addUser(User user) {
        List<User> users = loadUsers();
        users.add(user);
        saveUsers(users);
    }
}