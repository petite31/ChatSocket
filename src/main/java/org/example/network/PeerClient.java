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

    // 1. Chức năng gửi tin nhắn cá nhân (net send IP)
    public void sendText(String targetIP, String message) {
        MessagePacket packet = new MessagePacket(myIP, myName, message);
        sendPacket(targetIP, packet);
    }

    // 2. Chức năng gửi tin nhắn cho toàn mạng (net send *)
    public void sendTextToGroup(List<String> activeIPs, String message) {
        for (String ip : activeIPs) {
            sendText(ip, message);
        }
    }

    // 3. Chức năng gửi File/Ảnh
    public void sendFile(String targetIP, File file, boolean isImage) {
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            MessagePacket.Type type = isImage ? MessagePacket.Type.IMAGE : MessagePacket.Type.FILE;
            MessagePacket packet = new MessagePacket(type, myIP, myName, file.getName(), fileBytes);

            sendPacket(targetIP, packet);
        } catch (IOException e) {
            System.out.println("Lỗi đọc file: " + e.getMessage());
        }
    }

    // Hàm thực thi gửi TCP
    private void sendPacket(String targetIP, MessagePacket packet) {
        new Thread(() -> {
            try (Socket socket = new Socket(targetIP, targetPort);
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
                oos.writeObject(packet);
                oos.flush();
            } catch (IOException e) {
                System.out.println("Không thể kết nối đến " + targetIP);
            }
        }).start();
    }
}
