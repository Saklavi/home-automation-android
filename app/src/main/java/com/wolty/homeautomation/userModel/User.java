package com.wolty.homeautomation.userModel;

public final class User {
    private static int id;
    private static String name;
    private static String email;
    private static String token;

    public User(int _id, String _name, String _email, String _token) {
        id = _id;
        name = _name;
        email = _email;
        token = _token;
    }

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        User.id = id;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        User.name = name;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        User.email = email;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        User.token = token;
    }
}
