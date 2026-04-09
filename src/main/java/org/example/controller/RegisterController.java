package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import org.example.untils.AccountManager;

public class RegisterController {

    @FXML private TextField txtRegUsername;
    @FXML private PasswordField txtRegPassword;
    @FXML private TextField txtRegPort;

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = txtRegUsername.getText().trim();
        String password = txtRegPassword.getText().trim();
        String portStr = txtRegPort.getText().trim();

        if (username.isEmpty() || portStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập Tên hiển thị và Port!");
            return;
        }

        try {
            int port = Integer.parseInt(portStr);
            if (port < 1024 || port > 65535) {
                showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Port nên nằm trong khoảng 1024 - 65535.");
                return;
            }

            // Gọi hàm đăng ký thật sự lưu xuống thư mục "account"
            boolean isSuccess = AccountManager.register(username, password, port);

            if (isSuccess) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đăng ký thành công! Vui lòng đăng nhập.");
                goToLogin(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên tài khoản này đã tồn tại trên máy tính!");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Port phải là một số nguyên hợp lệ!");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể tải màn hình: " + e.getMessage());
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        // Tải lại màn hình Login.fxml
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 400, 500));
        stage.setTitle("P2P Chat - Đăng nhập");
        stage.centerOnScreen();
    }

    // Hàm hỗ trợ hiển thị Alert
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}