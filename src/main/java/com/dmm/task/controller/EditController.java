package com.dmm.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.dmm.task.data.entity.Task;
import com.dmm.task.service.TaskService;

@Controller
public class EditController {

    @Autowired
    private TaskService taskService;

    // タスク編集画面を表示
    @GetMapping("/main/edit/{id}")
    public String showEditPage(@PathVariable("id") Long id, Model model) {
        Task task = taskService.findById(id);
        model.addAttribute("task", task);
        return "edit";
    }

    // タスクを更新する
    @PostMapping("/main/edit")
    public String editTask(@ModelAttribute Task task) {
        taskService.saveTask(task); // タスクを保存
        return "redirect:/main"; // カレンダー画面へリダイレクト
    }

    // タスクを削除する
    @PostMapping("/main/delete/{id}")
    public String deleteTask(@PathVariable("id") Long id) {
        taskService.deleteTask(id); // タスクを削除
        return "redirect:/main"; // カレンダー画面へリダイレクト
    }
}
