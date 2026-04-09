package org.example.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.example.network.*;
import org.example.model.*;

public class DashboardController {

    @FXML private Label lblMyInfo;
    @FXML private TextArea txtGroupIPs;
    @FXML private TextField txtTargetIP;
    @FXML private TextField txtTargetPort;
    @FXML private ScrollPane chatScroll;
    @FXML private VBox chatBox;
    @FXML private TextField txtMessage;

    private String myIP;
    private String username;
    private int myPort;
    private PeerServer server;

    public void initData(String username, String myIP, int myPort) {
        this.username = username;
        this.myIP = myIP;
        this.myPort = myPort;
        lblMyInfo.setText("Tên: " + username + "\nIP: " + myIP + "\nPort: " + myPort);

        // Tự động cuộn xuống cuối khi có tin nhắn mới
        chatBox.heightProperty().addListener((observable, oldValue, newValue) -> chatScroll.setVvalue(1.0));

        startServer();
    }

    // --- HÀM VẼ BONG BÓNG CHAT ---
    private void addMessageBubble(String sender, String message, boolean isMine) {
        HBox container = new HBox();
        container.setPrefWidth(chatBox.getWidth());

        Label bubble = new Label(message);
        bubble.setWrapText(true);
        bubble.getStyleClass().add("message-bubble");

        if (isMine) {
            // Tin nhắn của mình: Nằm bên phải, màu xanh
            container.setAlignment(Pos.CENTER_RIGHT);
            bubble.getStyleClass().add("message-mine");
            container.getChildren().add(bubble);
        } else {
            // Tin nhắn người khác: Nằm bên trái, màu trắng
            container.setAlignment(Pos.CENTER_LEFT);
            bubble.getStyleClass().add("message-theirs");

            VBox wrapper = new VBox();
            Label nameLabel = new Label(sender);
            nameLabel.getStyleClass().add("sender-name");
            wrapper.getChildren().addAll(nameLabel, bubble);

            container.getChildren().add(wrapper);
        }

        chatBox.getChildren().add(container);
    }

    private void startServer() {
        server = new PeerServer(myPort, packet -> {
            Platform.runLater(() -> {
                if (packet.getType() == MessagePacket.Type.TEXT) {
                    addMessageBubble(packet.getSenderName(), packet.getTextContent(), false);
                } else {
                    addMessageBubble(packet.getSenderName(), "Đã gửi một file: " + packet.getFileName(), false);
                }
            });
        });
        server.setDaemon(true);
        server.start();

        // Lời chào hệ thống
        Platform.runLater(() -> {
            Label sysMsg = new Label("=== Đã sẵn sàng kết nối tại cổng " + myPort + " ===");
            sysMsg.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
            HBox sysBox = new HBox(sysMsg);
            sysBox.setAlignment(Pos.CENTER);
            chatBox.getChildren().add(sysBox);
        });
    }

    private List<String> getActiveGroupIPs() {
        String rawIPs = txtGroupIPs.getText().trim();
        if (rawIPs.isEmpty()) return new ArrayList<>();
        return Arrays.asList(rawIPs.split("\\s*,\\s*"));
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
            List<String> groupList = getActiveGroupIPs();
            if (groupList.isEmpty()) return;
            client.sendTextToGroup(groupList, message);
        } else {
            client.sendText(targetIP, message);
        }

        // Vẽ tin nhắn lên màn hình của mình
        addMessageBubble(username, message, true);
        txtMessage.clear();
    }

    @FXML
    private void handleSendFile() {
        String targetIP = txtTargetIP.getText().trim();
        String targetPortStr = txtTargetPort.getText().trim();

        if (targetIP.isEmpty() || targetPortStr.isEmpty()) return;

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            int targetPort = Integer.parseInt(targetPortStr);
            PeerClient client = new PeerClient(myIP, username, targetPort);

            if (targetIP.equals("*")) {
                for (String ip : getActiveGroupIPs()) {
                    client.sendFile(ip, file, false, null); // Thêm hiệu ứng
                }
            } else {
                client.sendFile(targetIP, file, false, null);
            }

            // Hiện tin nhắn file lên màn hình của mình
            addMessageBubble(username, "Bạn đã gửi file:\n" + file.getName(), true);
        }
    }
}