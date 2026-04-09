module org.example.chatsocket {
    // 1. Khai báo các thư viện (module) cần thiết để chạy app
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.sql; // Gson đôi khi cần thư viện này ngầm để parse ngày tháng

    // Cấp quyền cho JavaFX truy cập (Reflection) vào các file giao diện
    opens org.example to javafx.fxml;
    opens org.example.controller to javafx.fxml;

    // Cấp quyền cho Gson tự do đóng/mở gói các Object Model thành file JSON
    opens org.example.model to com.google.gson;

    // Xuất các gói để JVM có thể nhìn thấy
    exports org.example;
    exports org.example.controller;
    exports org.example.model;
    exports org.example.network;
    exports org.example.untils;
}