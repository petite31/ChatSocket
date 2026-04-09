package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.InetAddress;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private TextField txtPort;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String portStr = txtPort.getText().trim();

        if (username.isEmpty() || portStr.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập đủ thông tin!");
            return;
        }

        try {
            int port = Integer.parseInt(portStr);
            String myIP = InetAddress.getLocalHost().getHostAddress(); // Lấy IP máy hiện tại

            // Chuyển sang màn hình Dashboard và truyền dữ liệu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dashboard = loader.getController();
            dashboard.initData(username, myIP, port);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 700));
            stage.setTitle(username + "Chat App");
            stage.centerOnScreen();

        } catch (Exception e) {
            showAlert("Lỗi", "Port phải là số hoặc lỗi mạng: " + e.getMessage());
        }
    }

    @FXML
    private void goToRegister(ActionEvent event) throws IOException {
        // Trong P2P thực tế có thể bỏ qua bước này, nhưng làm theo yêu cầu của bạn
        Parent root = FXMLLoader.load(getClass().getResource("/Register.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 400, 500));
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}