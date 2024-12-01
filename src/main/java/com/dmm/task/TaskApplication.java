package com.dmm.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class TaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskApplication.class, args);

        // ハッシュ化したいパスワードを入力（admin と user）
        String[] rawPasswords = {"admin", "user"};

        // パスワードをハッシュ化し、表示
        for (String rawPassword : rawPasswords) {
            String password = getEncodePassword(rawPassword);
            System.out.println("Raw Password: " + rawPassword + " -> Encoded: " + password);
        }
    }

    // パスワードをハッシュ化するメソッド
    private static String getEncodePassword(String rawPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(rawPassword);
    }
}

