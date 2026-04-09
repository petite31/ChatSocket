package org.example.network;

import org.example.model.MessagePacket;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class PeerServer extends Thread {
    private int port;
    private Consumer<MessagePacket> onPacketReceived; // Truyền nguyên gói tin lên UI

    public PeerServer(int port, Consumer<MessagePacket> onPacketReceived) {
        this.port = port;
        this.onPacketReceived = onPacketReceived;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server đang chạy tại port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket)).start(); // Xử lý đa luồng cho nhiều người gửi cùng lúc
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            MessagePacket packet = (MessagePacket) ois.readObject();

            // Xử lý lưu file tự động nếu là FILE hoặc IMAGE
            if (packet.getType() == MessagePacket.Type.FILE || packet.getType() == MessagePacket.Type.IMAGE) {
                saveFileToDisk(packet);
            }

            // Đẩy dữ liệu lên JavaFX Controller
            if (onPacketReceived != null) {
                onPacketReceived.accept(packet);
            }
        } catch (Exception e) {
            System.out.println("Lỗi nhận dữ liệu: " + e.getMessage());
        }
    }

    private void saveFileToDisk(MessagePacket packet) {
        File downloadDir = new File("Downloads_P2P");
        if (!downloadDir.exists()) downloadDir.mkdir();

        File newFile = new File(downloadDir, packet.getFileName());
        try (FileOutputStream fos = new FileOutputStream(newFile)) {
            fos.write(packet.getFileData());
            System.out.println("Đã lưu file tại: " + newFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}