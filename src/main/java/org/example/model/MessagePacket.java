package org.example.model;

import java.io.Serializable;

public class MessagePacket implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type { TEXT, FILE, IMAGE }

    private Type type;
    private String senderIP;
    private String senderName;
    private String textContent;   // Dùng cho tin nhắn text
    private String fileName;      // Dùng cho file/ảnh
    private byte[] fileData;      // Nội dung file được băm thành byte

    // Constructor cho Text
    public MessagePacket(String senderIP, String senderName, String textContent) {
        this.type = Type.TEXT;
        this.senderIP = senderIP;
        this.senderName = senderName;
        this.textContent = textContent;
    }

    // Constructor cho File/Ảnh
    public MessagePacket(Type type, String senderIP, String senderName, String fileName, byte[] fileData) {
        this.type = type;
        this.senderIP = senderIP;
        this.senderName = senderName;
        this.fileName = fileName;
        this.fileData = fileData;
    }

    // Các Getter (bạn tự generate bằng IDE nhé)
    public Type getType() { return type; }
    public String getSenderIP() { return senderIP; }
    public String getSenderName() { return senderName; }
    public String getTextContent() { return textContent; }
    public String getFileName() { return fileName; }
    public byte[] getFileData() { return fileData; }
}
