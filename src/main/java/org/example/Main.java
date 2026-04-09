package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            // Tải file giao diện Login.fxml từ thư mục resources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();

            // Tạo Scene với kích thước khớp với thiết kế trong FXML
            Scene scene = new Scene(root, 400, 500);

            // Thiết lập Stage (Cửa sổ ứng dụng)
            primaryStage.setTitle("Eira Chat App");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); // Khóa kích thước cửa sổ đăng nhập cho đẹp

            // Hiển thị ở giữa màn hình
            primaryStage.centerOnScreen();
            primaryStage.show();
        }


        public static void main(String[] args) {
            launch(args);
        }

    }