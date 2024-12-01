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

		// カレンダーのデータを作成
		List<List<LocalDate>> calendar = generateCalendar(currentDate);

		// 月の開始日と終了日を計算
		LocalDate startDate = currentDate.withDayOfMonth(1);
		LocalDate endDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

		// タスクを取得
		List<Task> taskList = isAdmin
				? taskService.getTasksByMonthForAdmin(startDate, endDate)
				: taskService.getTasksByMonthAndUser(startDate, endDate, userName);

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

	private List<List<LocalDate>> generateCalendar(LocalDate currentDate) {
		List<List<LocalDate>> calendar = new ArrayList<>();
		LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1); // 当月の1日
		DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek(); // 1日の曜日を取得
		LocalDate startDay = firstDayOfMonth.minusDays(firstDayOfWeek.getValue() % 7); // カレンダーの開始日を計算

		while (true) {
			List<LocalDate> week = new ArrayList<>();
			for (int i = 0; i < 7; i++) { // 1週間分の日付を追加
				week.add(startDay);
				startDay = startDay.plusDays(1);
			}

			calendar.add(week);

			// カレンダーの終了条件
			LocalDate lastDayOfWeek = week.get(6); // 週の最後の日(土曜日)
			if (lastDayOfWeek.isAfter(currentDate.withDayOfMonth(currentDate.lengthOfMonth())) // 当月の最終日を超えている
					&& lastDayOfWeek.getDayOfWeek() == DayOfWeek.SATURDAY) { // かつ土曜日の場合終了
				break;
			}
		}

		return calendar;
	}

}
