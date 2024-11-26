package com.dmm.task.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CreateController {

    @PostMapping("/main/create")
    public String createPost(@RequestParam String title, 
                             @RequestParam String date, 
                             @RequestParam String text, 
                             Model model) {
        // 登録処理（例: データベースに保存）
        
        // 登録後のメッセージをビューに渡す
        model.addAttribute("message", "新しい投稿が作成されました！");

        return "redirect:/main";  // メインページにリダイレクト
    }
}
