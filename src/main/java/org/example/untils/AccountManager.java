package org.example.untils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.example.model.UserProfile;

public class AccountManager {
        private static final String ACCOUNT_DIR = "account";
        private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Khởi tạo thư mục nếu chưa có
        static {
            try {
                Path path = Paths.get(ACCOUNT_DIR);
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Hàm đăng ký: Trả về true nếu thành công, false nếu user đã tồn tại
        public static boolean register(String username, String password, int port) {
            File userFile = new File(ACCOUNT_DIR + "/" + username.toLowerCase() + ".json");

            if (userFile.exists()) {
                return false; // Tài khoản đã tồn tại
            }

            try (Writer writer = new FileWriter(userFile)) {
                UserProfile profile = new UserProfile(username, password, port);
                gson.toJson(profile, writer);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        // Hàm đăng nhập: Trả về UserProfile nếu đúng, null nếu sai hoặc không tồn tại
        public static UserProfile login(String username, String password) {
            File userFile = new File(ACCOUNT_DIR + "/" + username.toLowerCase() + ".json");

            if (!userFile.exists()) {
                return null; // Không tìm thấy tài khoản
            }

            try (Reader reader = new FileReader(userFile)) {
                UserProfile profile = gson.fromJson(reader, UserProfile.class);
                // Kiểm tra mật khẩu (nếu có nhập mật khẩu)
                if (profile.getPassword().equals(password)) {
                    return profile;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
