package org.example.network;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;
import org.example.model.MessagePacket;

public class PeerClient {
    private int targetPort;
    private String myIP;
    private String myName;

    public PeerClient(String myIP, String myName, int targetPort) {
        this.myIP = myIP;
        this.myName = myName;
        this.targetPort = targetPort;
    }

    // 1. Chức năng gửi tin nhắn cá nhân (không cần callback)
    public void sendText(String targetIP, String message) {
        MessagePacket packet = new MessagePacket(myIP, myName, message);
        sendPacket(targetIP, packet, null);
    }

    // 2. Chức năng gửi tin nhắn cho toàn mạng
    public void sendTextToGroup(List<String> activeIPs, String message) {
        for (String ip : activeIPs) {
            sendText(ip, message);
        }
    }

    // 3. Chức năng gửi File/Ảnh (Có thêm Runnable onSuccess để báo hoàn thành)
    public void sendFile(String targetIP, File file, boolean isImage, Runnable onSuccess) {
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            MessagePacket.Type type = isImage ? MessagePacket.Type.IMAGE : MessagePacket.Type.FILE;
            MessagePacket packet = new MessagePacket(type, myIP, myName, file.getName(), fileBytes);

            sendPacket(targetIP, packet, onSuccess);
        } catch (IOException e) {
            System.out.println("Lỗi đọc file: " + e.getMessage());
        }
    }

    // Hàm thực thi gửi TCP cốt lõi
    private void sendPacket(String targetIP, MessagePacket packet, Runnable onSuccess) {
        new Thread(() -> {
            try (Socket socket = new Socket(targetIP, targetPort);
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
                oos.writeObject(packet);
                oos.flush();

                // Nếu quá trình gửi không văng lỗi, chạy hàm callback để báo UI
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } catch (IOException e) {
                System.out.println("Không thể kết nối đến " + targetIP);
            }
        }).start();
    }
}