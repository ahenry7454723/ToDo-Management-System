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

        List<List<LocalDate>> calendar = generateCalendar(currentDate);

        MultiValueMap<LocalDate, Task> tasks = new LinkedMultiValueMap<>();
        List<Task> taskList = isAdmin ? taskService.findAllTasks() : taskService.getTasksByUser(userName);

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

    private List<List<LocalDate>> generateCalendar(LocalDate currentDate) {
        List<List<LocalDate>> calendar = new ArrayList<>();
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek();
        LocalDate startDay = firstDayOfMonth.minusDays(firstDayOfWeek.getValue() % 7);

        for (int i = 0; i < 6; i++) { // 最大6週
            List<LocalDate> week = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                week.add(startDay);
                startDay = startDay.plusDays(1);
            }
            calendar.add(week);

            // 次の週が当月外になった場合、ループを終了
            if (startDay.isAfter(lastDayOfMonth) && startDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
                break;
            }
        }
        return calendar;
    }
}
