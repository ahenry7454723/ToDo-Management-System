package com.dmm.task.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dmm.task.data.entity.Task;
import com.dmm.task.service.AccountUserDetails;
import com.dmm.task.service.TaskService;

@Controller
@RequestMapping("/main") // 共通のURLパス (/main) を定義
public class CreateController {

    @Autowired
    private TaskService taskService;

    /**
     * タスク登録画面を表示するメソッド
     * 
     * @param date  選択した日付 (yyyy-MM-dd形式の文字列)
     * @param model ビューにデータを渡すためのオブジェクト
     * @return タスク登録画面のHTMLテンプレート名
     */
    @GetMapping("/create/{date}")
    public String showCreatePage(@PathVariable("date") String date, Model model) {
        try {
            // 日付文字列をLocalDateに変換
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate parsedDate = LocalDate.parse(date, formatter);

            // モデルに日付を追加
            model.addAttribute("date", parsedDate);
        } catch (DateTimeParseException e) {
            // 日付の解析失敗時、エラーメッセージを設定
            model.addAttribute("errorMessage", "日付のフォーマットが正しくありません。");
            return "error"; // エラーページに遷移
        }

        // 新しいタスクオブジェクトをビューに渡す（初期化用）
        model.addAttribute("task", new Task());
        return "create"; // タスク登録画面（create.html）
    }

    /**
     * タスクを保存するメソッド
     * 
     * @param task 入力されたタスク情報
     * @param redirectAttributes リダイレクト先にメッセージを渡すためのオブジェクト
     * @return カレンダー画面へのリダイレクトURL
     */
    @PostMapping("/create")
    public String createTask(@ModelAttribute Task task, RedirectAttributes redirectAttributes) {
        // ログイン中のユーザー情報を取得
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AccountUserDetails) {
            AccountUserDetails userDetails = (AccountUserDetails) principal;
            String userName = userDetails.getUsername();

            // タスクの作成者名を設定
            task.setName(userName);
        } else {
            // ユーザー情報が取得できない場合の処理
            redirectAttributes.addFlashAttribute("errorMessage", "ログイン情報が確認できません。再ログインしてください。");
            return "redirect:/loginForm";
        }

        // タスクを保存
        taskService.saveTask(task);

        // リダイレクト先に成功メッセージを渡す
        redirectAttributes.addFlashAttribute("successMessage", "タスクが正常に登録されました。");
        return "redirect:/main"; // カレンダー画面にリダイレクト
    }
}
