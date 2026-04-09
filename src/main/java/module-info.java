module org.example.chatsocket {
    // 1. Khai báo các thư viện (module) cần thiết để chạy app
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    // 2. Cho phép JavaFX truy cập vào các file Controller để tiêm (inject) các biến @FXML
    opens org.example.controller to javafx.fxml;
    opens org.example to javafx.fxml;

    // 3. Cho phép thư viện Gson truy xuất vào class Model để chuyển đổi JSON (Lưu/Đọc tài khoản)
    opens org.example.model to com.google.gson;

    // 4. Xuất (Export) các package để các module khác (như JavaFX) có thể nhìn thấy
    exports org.example;
    exports org.example.controller;
    exports org.example.model;
    exports org.example.network;
    exports org.example.untils;
}