package org.example.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.example.network.*;
import org.example.model.*;

public class DashboardController {

    @FXML private Label lblMyInfo;
    @FXML private TextField txtTargetIP;
    @FXML private TextField txtTargetPort;
    @FXML private TextArea chatArea;
    @FXML private TextField txtMessage;

    private String myIP;
    private String username;
    private int myPort;
    private PeerServer server;

    // Giả lập danh sách IP trong mạng LAN (dành cho tính năng net send *)
    private final List<String> groupIPs = Arrays.asList("127.0.0.1", "192.168.1.5");

    // Hàm này được gọi từ LoginController
    public void initData(String username, String myIP, int myPort) {
        this.username = username;
        this.myIP = myIP;
        this.myPort = myPort;

        lblMyInfo.setText("Tên: " + username + "\nIP: " + myIP + "\nPort: " + myPort);

        // Khởi động Server lắng nghe tin nhắn đến
        startServer();
    }

    private void startServer() {
        server = new PeerServer(myPort, packet -> {
            // Cập nhật UI phải dùng Platform.runLater
            Platform.runLater(() -> {
                if (packet.getType() == MessagePacket.Type.TEXT) {
                    chatArea.appendText("[" + packet.getSenderIP() + "] " + packet.getSenderName() + ": " + packet.getTextContent() + "\n");
                } else {
                    chatArea.appendText("[" + packet.getSenderIP() + "] " + packet.getSenderName() + " đã gửi file: " + packet.getFileName() + " (Đã lưu vào thư mục Downloads_P2P)\n");
                }
            });
        });
        server.setDaemon(true); // Để thread tự tắt khi đóng ứng dụng
        server.start();
        chatArea.appendText("=== Hệ thống đang lắng nghe tại cổng " + myPort + " ===\n");
    }

    @FXML
    private void handleSendMessage() {
        String targetIP = txtTargetIP.getText().trim();
        String targetPortStr = txtTargetPort.getText().trim();
        String message = txtMessage.getText().trim();

        if (targetIP.isEmpty() || targetPortStr.isEmpty() || message.isEmpty()) return;

        int targetPort = Integer.parseInt(targetPortStr);
        PeerClient client = new PeerClient(myIP, username, targetPort);

        if (targetIP.equals("*")) {
            // Gửi cho tất cả máy (net send *)
            client.sendTextToGroup(groupIPs, message);
            chatArea.appendText("[Tôi -> Mọi người]: " + message + "\n");
        } else {
            // Gửi cá nhân
            client.sendText(targetIP, message);
            chatArea.appendText("[Tôi -> " + targetIP + ":" + targetPort + "]: " + message + "\n");
        }
        txtMessage.clear();
    }

    @FXML
    private void handleSendFile() {
        String targetIP = txtTargetIP.getText().trim();
        String targetPortStr = txtTargetPort.getText().trim();

        if (targetIP.isEmpty() || targetIP.equals("*") || targetPortStr.isEmpty()) {
            chatArea.appendText("Vui lòng nhập chính xác IP và Port đích để gửi file (Không hỗ trợ gửi file cho group *).\n");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file để gửi");
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            int targetPort = Integer.parseInt(targetPortStr);
            PeerClient client = new PeerClient(myIP, username, targetPort);

            client.sendFile(targetIP, file, false);
            chatArea.appendText("[Tôi -> " + targetIP + "]: Đang gửi file " + file.getName() + "...\n");
        }
    }
}
