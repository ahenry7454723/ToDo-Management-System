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

    @GetMapping("/main")
    public String showMainPage(@RequestParam(required = false) String date, Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof AccountUserDetails)) {
            return "redirect:/loginForm";
        }

        AccountUserDetails userDetails = (AccountUserDetails) principal;
        String userName = userDetails.getUsername();

        Users user = usersRepository.findByUserName(userName);
        boolean isAdmin = user != null && "ADMIN".equals(user.getRoleName());

        LocalDate currentDate = (date == null) ? LocalDate.now() : LocalDate.parse(date);

        // カレンダーのデータを作成し、範囲を取得
        LocalDate[] calendarRange = new LocalDate[2];
        List<List<LocalDate>> calendar = generateCalendar(currentDate, calendarRange);

        LocalDate startDay = calendarRange[0]; // カレンダーの開始日
        LocalDate endDay = calendarRange[1];   // カレンダーの終了日

        // タスクを取得
        List<Task> taskList = isAdmin
                ? taskService.getTasksByDateRange(startDay, endDay)
                : taskService.getTasksByDateRangeAndUser(startDay, endDay, userName);

        // 日付とタスクを紐付けるマップを作成
        MultiValueMap<LocalDate, Task> tasks = new LinkedMultiValueMap<>();
        for (Task task : taskList) {
            tasks.add(task.getDate(), task);
        }

        model.addAttribute("matrix", calendar);
        model.addAttribute("tasks", tasks);

        LocalDate prev = currentDate.minusMonths(1).withDayOfMonth(1);
        LocalDate next = currentDate.plusMonths(1).withDayOfMonth(1);
        model.addAttribute("prev", prev);
        model.addAttribute("next", next);

        String currentMonth = currentDate.format(DateTimeFormatter.ofPattern("yyyy年MM月"));
        model.addAttribute("month", currentMonth);

        return "main";
    }

    private List<List<LocalDate>> generateCalendar(LocalDate currentDate, LocalDate[] calendarRange) {
        List<List<LocalDate>> calendar = new ArrayList<>();
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1); // 月初日
        DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek(); // 月初日の曜日
        LocalDate startDay = firstDayOfMonth.minusDays(firstDayOfWeek.getValue() % 7); // カレンダーの開始日（前月の日曜日）

        calendarRange[0] = startDay; // カレンダー範囲の開始日

        while (true) {
            List<LocalDate> week = new ArrayList<>();
            for (int i = 0; i < 7; i++) { // 1週間分の日付を追加
                week.add(startDay);
                startDay = startDay.plusDays(1);
            }
            calendar.add(week);

            // 最終日を計算
            LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

            // カレンダー終了条件
            if (week.contains(lastDayOfMonth) && startDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
                calendarRange[1] = startDay.minusDays(1); // カレンダー範囲の終了日
                break; // 最終日を含む週までカレンダーを表示
            }
        }

        return calendar;
    }
}
