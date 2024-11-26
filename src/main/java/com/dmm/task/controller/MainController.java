package com.dmm.task.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    /**
     * カレンダー表示処理
     *
     * @param dateStr URLパラメータとして渡された日付（形式: "yyyy-MM-dd"）。省略時は現在の日付を使用。
     * @param model Thymeleafテンプレートに渡すデータを保持するオブジェクト。
     * @return カレンダー表示用のテンプレート名 ("calendar")。
     */
    @GetMapping("/main")
    public String showCalendar(
            @RequestParam(name = "date", required = false) String dateStr, 
            Model model) {
        
        // 現在の日付、または指定された日付を取得
        LocalDate currentDate = dateStr == null ? LocalDate.now() : LocalDate.parse(dateStr);
        
        // カレンダーの行列データを生成
        List<List<LocalDate>> calendarMatrix = generateCalendarMatrix(currentDate);

        // タスクの仮データを取得（実際のデータベース接続部分はこの部分を置き換える）
        Map<LocalDate, List<Task>> tasks = getDummyTasks();

        // モデルにデータを登録
        model.addAttribute("matrix", calendarMatrix); // カレンダーの行列データ
        model.addAttribute("tasks", tasks);           // 各日のタスクデータ
        model.addAttribute("month", currentDate.getYear() + "年" + currentDate.getMonthValue() + "月"); // 年月表示
        model.addAttribute("prev", currentDate.minusMonths(1).withDayOfMonth(1)); // 前月の1日
        model.addAttribute("next", currentDate.plusMonths(1).withDayOfMonth(1));  // 翌月の1日

        return "calendar"; // "calendar.html"テンプレートを表示
    }

    /**
     * カレンダーの行列データを生成
     *
     * @param date 基準となる月の日付
     * @return カレンダーの週ごとのリスト（1週間に7日分のデータ）
     */
    private List<List<LocalDate>> generateCalendarMatrix(LocalDate date) {
        List<List<LocalDate>> calendar = new ArrayList<>();
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);

        // 月の初日が含まれる週の日曜日を計算
        LocalDate start = firstDayOfMonth.minusDays(firstDayOfMonth.getDayOfWeek().getValue() % 7);

        // 各週を生成
        while (true) {
            List<LocalDate> week = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                week.add(start);
                start = start.plusDays(1);
            }
            calendar.add(week);
            if (week.get(6).getMonthValue() != date.getMonthValue()) {
                break; // 次の月に入ったら終了
            }
        }
        return calendar;
    }

    /**
     * タスクの仮データを生成（デモ用）
     *
     * @return 各日のタスクをマップとして返す
     */
    private Map<LocalDate, List<Task>> getDummyTasks() {
        Map<LocalDate, List<Task>> tasks = new HashMap<>();
        LocalDate now = LocalDate.now();

        tasks.put(now, List.of(new Task(1, "猫の餌を買う", true)));
        tasks.put(now.plusDays(1), List.of(new Task(2, "ノートを買う", false), new Task(3, "ボールペンを買う", false)));
        tasks.put(now.plusDays(3), List.of(new Task(4, "牛乳を買う", false)));
        tasks.put(now.plusDays(7), List.of(new Task(5, "課題を進める", false)));

        return tasks;
    }

    /**
     * タスクのデータクラス
     */
    public static class Task {
        private final int id;      // タスクID
        private final String title; // タスク名
        private final boolean done; // 完了フラグ

        public Task(int id, String title, boolean done) {
            this.id = id;
            this.title = title;
            this.done = done;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public boolean isDone() {
            return done;
        }
    }
}