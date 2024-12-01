package com.dmm.task.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dmm.task.data.entity.Task;
import com.dmm.task.service.AccountUserDetails;
import com.dmm.task.service.TaskService;

@Controller
@RequestMapping("/main") // 共通URLパス（/main）を定義
public class CreateController {

    @Autowired
    private TaskService taskService;

    /**
     * タスク登録画面を表示するメソッド
     * 
     * @param date  選択した日付（yyyy-MM-dd形式の文字列）
     * @param model ビューに渡すデータを格納するオブジェクト
     * @return タスク登録画面のHTMLテンプレート名
     */
    @GetMapping("/create/{date}")
    public String showCreatePage(@PathVariable("date") String date, Model model) {
        try {
            // 入力された日付（文字列）をLocalDate型に変換
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate parsedDate = LocalDate.parse(date, formatter);

            // 変換した日付をモデルに追加（ビューで使用）
            model.addAttribute("date", parsedDate);
        } catch (Exception e) {
            // 日付の解析に失敗した場合のエラーメッセージ出力
            System.err.println("日付の解析に失敗しました: " + e.getMessage());
            return "error"; // エラー画面を表示する
        }

        return "create"; // タスク登録画面（create.html）を返す
    }

    /**
     * タスクを保存するメソッド
     * 
     * @param task ユーザーから入力されたタスク情報
     * @return カレンダー画面へのリダイレクトURL
     */
    @PostMapping("/create")
    public String createTask(@ModelAttribute Task task) {
        // ログイン中のユーザー名を取得
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AccountUserDetails) {
            AccountUserDetails userDetails = (AccountUserDetails) principal;
            String userName = userDetails.getUsername();

            // タスクの name フィールドにユーザー名を設定
            task.setName(userName);
        }

        // タスクを保存
        taskService.saveTask(task);
        return "redirect:/main"; // 登録後にカレンダー画面へリダイレクト
    }

}
