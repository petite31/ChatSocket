package org.example.model;

public class UserProfile {
    private String username;
    private String password;
    private int defaultPort;

    public UserProfile(String username, String password, int defaultPort) {
        this.username = username;
        this.password = password;
        this.defaultPort = defaultPort;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getDefaultPort() { return defaultPort; }
}

