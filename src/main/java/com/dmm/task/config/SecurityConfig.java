package com.dmm.task.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(auth -> auth
                .anyRequest().authenticated() // 全てのリクエストを認証必須
            )
            .formLogin(login -> login
                .loginPage("/loginForm")        // カスタムログインページ
                .loginProcessingUrl("/authenticate") // フォームのactionに対応
                .permitAll()                   // ログインページは認証不要
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/loginForm") // ログアウト後はログイン画面へ
                .permitAll()
            );
        return http.build();
    }
}
