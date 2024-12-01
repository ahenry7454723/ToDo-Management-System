package com.dmm.task.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dmm.task.data.entity.Task;
import com.dmm.task.data.entity.Users;
import com.dmm.task.data.repository.UsersRepository;
import com.dmm.task.service.AccountUserDetails;
import com.dmm.task.service.TaskService;

@Controller
public class MainController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UsersRepository usersRepository;

    /**
     * カレンダー表示画面
     *
     * @param date  表示する月の任意の日付（省略時は現在日付）
     * @param model ビューにデータを渡すモデル
     * @return カレンダー画面のテンプレート名
     */
    @GetMapping("/main")
    public String showMainPage(@RequestParam(required = false) String date, Model model) {
        // 現在のログインユーザー情報を取得
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof AccountUserDetails)) {
            return "redirect:/loginForm"; // ログイン画面にリダイレクト
        }

        AccountUserDetails userDetails = (AccountUserDetails) principal;
        String userName = userDetails.getUsername();

        // ユーザー情報を取得してロール確認
        Users user = usersRepository.findByUserName(userName);
        boolean isAdmin = user != null && "ADMIN".equals(user.getRoleName());

        // 現在の日付または指定された日付
        LocalDate currentDate = (date == null) ? LocalDate.now() : LocalDate.parse(date);

        // カレンダーのデータを作成
        List<List<LocalDate>> calendar = generateCalendar(currentDate);

        // 日付とタスクを紐付けるマップを作成
        MultiValueMap<LocalDate, Task> tasks = new LinkedMultiValueMap<>();
        List<Task> taskList = isAdmin ? taskService.findAllTasks() : taskService.getTasksByUser(userName);

        for (Task task : taskList) {
            tasks.add(task.getDate(), task);
        }

        // モデルにデータを追加
        model.addAttribute("matrix", calendar);
        model.addAttribute("tasks", tasks);

        // 前月と次月を計算してモデルに追加
        LocalDate prev = currentDate.minusMonths(1).withDayOfMonth(1);
        LocalDate next = currentDate.plusMonths(1).withDayOfMonth(1);
        model.addAttribute("prev", prev);
        model.addAttribute("next", next);

        // 現在の月の表示用フォーマット
        String currentMonth = currentDate.format(DateTimeFormatter.ofPattern("yyyy年MM月"));
        model.addAttribute("month", currentMonth); // ビューで表示

        return "main"; // main.htmlを表示
    }

    /**
     * カレンダーのデータを生成する
     *
     * @param currentDate 表示する月の任意の日付
     * @return カレンダーのデータ
     */
    private List<List<LocalDate>> generateCalendar(LocalDate currentDate) {
        List<List<LocalDate>> calendar = new ArrayList<>();
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek();
        LocalDate startDay = firstDayOfMonth.minusDays(firstDayOfWeek.getValue() % 7);

        for (int i = 0; i < 6; i++) { // 最大6行
            List<LocalDate> week = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                week.add(startDay);
                startDay = startDay.plusDays(1);
            }
            calendar.add(week);
        }
        return calendar;
    }
}
