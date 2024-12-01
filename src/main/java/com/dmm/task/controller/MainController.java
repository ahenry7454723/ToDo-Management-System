package com.dmm.task.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dmm.task.data.entity.Task;
import com.dmm.task.service.AccountUserDetails;
import com.dmm.task.service.TaskService;

@Controller // コントローラークラスであることを指定
public class MainController {

    @Autowired
    private TaskService taskService; // タスク情報を操作するサービスクラスを注入

    /**
     * メインページを表示するためのハンドラメソッド
     * @param model ビューにデータを渡すためのオブジェクト
     * @return メインページ (main.html)
     */
    @GetMapping("/main")
    public String showMainPage(@RequestParam(required = false) String date, Model model) {
        // ログインしているユーザー情報を取得
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof AccountUserDetails)) {
            return "redirect:/loginForm";
        }

        AccountUserDetails userDetails = (AccountUserDetails) principal;
        String userName = userDetails.getUsername();

        // 現在の日付またはリクエストで指定された日付
        LocalDate currentDate = (date == null) ? LocalDate.now() : LocalDate.parse(date);

        // カレンダー生成
        List<List<LocalDate>> calendar = generateCalendar(currentDate);

        // 前月と次月の取得
        LocalDate prev = currentDate.minusMonths(1).withDayOfMonth(1);
        LocalDate next = currentDate.plusMonths(1).withDayOfMonth(1);

        // ユーザーのタスクを取得
        List<Task> userTasks = taskService.getTasksByUser(userName);
        List<LocalDate> taskDates = extractTaskDates(userTasks);

        // 現在の月をフォーマット（例: "2024年11月"）
        String currentMonth = currentDate.format(DateTimeFormatter.ofPattern("yyyy年MM月"));

        // 必要なデータをビューに渡す
        model.addAttribute("userName", userName);
        model.addAttribute("calendar", calendar);
        model.addAttribute("taskDates", taskDates);
        model.addAttribute("taskMap", createTaskMap(userTasks));
        model.addAttribute("prev", prev);
        model.addAttribute("next", next);
        model.addAttribute("currentMonth", currentMonth);

        return "main";
    }

    // Taskを日付ごとにマップ化するユーティリティ
    private Map<LocalDate, List<Task>> createTaskMap(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> task.getDate() != null)
                .collect(Collectors.groupingBy(Task::getDate));
    }
    /**
     * カレンダーを生成するメソッド
     * 現在の月を基準に週ごとの日付をリストとして作成します。
     * @param currentDate 現在の日付
     * @return カレンダー (週ごとに日付を含むリスト)
     */
    private List<List<LocalDate>> generateCalendar(LocalDate currentDate) {
        List<List<LocalDate>> calendar = new ArrayList<>();

        // 現在の月の最初の日を取得
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);

        // その日の曜日を取得 (例: 月曜日)
        DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek();

        // カレンダーの開始日を前月の週から調整
        LocalDate currentDay = firstDayOfMonth.minusDays(firstDayOfWeek.getValue() % 7);

        // カレンダーを6行（週単位）で生成
        for (int i = 0; i < 6; i++) { // 最大6行
            List<LocalDate> week = new ArrayList<>();
            for (int j = 0; j < 7; j++) { // 1週間は7日
                week.add(currentDay); // 現在の日付を追加
                currentDay = currentDay.plusDays(1); // 次の日に進む
            }
            calendar.add(week); // 1週間をカレンダーに追加
            // 翌月に進んだらループ終了
            if (currentDay.getMonth() != currentDate.getMonth()) {
                break;
            }
        }

        return calendar; // 作成したカレンダーを返す
    }

    /**
     * タスクの日付リストを抽出するメソッド
     * @param tasks ユーザーのタスクリスト
     * @return タスクの日付を含むリスト
     */
    private List<LocalDate> extractTaskDates(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return Collections.emptyList(); // タスクがなければ空のリストを返す
        }

        // タスクの日付をリストに追加
        List<LocalDate> taskDates = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDate() != null) { // タスクの日付がnullでない場合
                taskDates.add(task.getDate());
            }
        }
        return taskDates; // 日付リストを返す
    }

    /**
     * 月と年をフォーマットして文字列を作成
     * @param date 現在の日付
     * @return フォーマットされた月と年の文字列 (例: "2024年 12月")
     */
    private String formatMonthYear(LocalDate date) {
        return date.getYear() + "年 " + date.getMonthValue() + "月";
    }
}
