package com.dmm.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.dmm.task.data.entity.Task;
import com.dmm.task.service.AccountUserDetails;
import com.dmm.task.service.TaskService;

@Controller
public class EditController {

	@Autowired
	private TaskService taskService;

	/**
	 * タスク編集画面を表示する
	 * @param id タスクID
	 * @param model モデルオブジェクト
	 * @return 編集画面 (edit.html)
	 */
	@GetMapping("/main/edit/{id}")
	public String showEditPage(@PathVariable("id") Long id, Model model) {
		// タスクをIDで検索
		Task task = taskService.findById(id);
		if (task == null) {
			// タスクが見つからない場合、エラーページにリダイレクト
			model.addAttribute("errorMessage", "指定されたタスクは存在しません。");
			return "error"; // エラーページ (error.html) に遷移
		}

		// タスクをモデルに追加
		model.addAttribute("task", task);
		return "edit"; // 編集画面に遷移
	}

	/**
	 * タスクを更新する
	 * @param id タスクID
	 * @param task 更新するタスク情報
	 * @return カレンダー画面にリダイレクト
	 */
	@PostMapping("/main/edit/{id}")
	public String editTask(@PathVariable("id") Long id, @ModelAttribute Task task) {
	    // ログイン中のユーザー名を取得
	    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    if (principal instanceof AccountUserDetails) {
	        AccountUserDetails userDetails = (AccountUserDetails) principal;
	        String userName = userDetails.getUsername();
	        task.setName(userName); // タスクにユーザー名を設定
	    } else {
	        // エラー処理（必要に応じて）
	        return "redirect:/loginForm";
	    }

	    // タスクのIDを明示的に設定
	    task.setId(id);

	    // タスクを保存
	    taskService.saveTask(task);

	    return "redirect:/main"; // カレンダー画面にリダイレクト
	}

	/**
	 * タスクを削除する
	 * @param id タスクID
	 * @return カレンダー画面にリダイレクト
	 */
	@PostMapping("/main/delete/{id}")
	public String deleteTask(@PathVariable("id") Long id) {
		// タスクを削除
		taskService.deleteTask(id);
		return "redirect:/main"; // カレンダー画面にリダイレクト
	}
}
