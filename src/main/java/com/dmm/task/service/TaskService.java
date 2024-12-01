package com.dmm.task.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dmm.task.data.entity.Task;
import com.dmm.task.data.repository.TaskRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public void saveTask(Task task) {
        taskRepository.save(task);
    }

    public Task findById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> getTasksByUser(String userName) {
        return taskRepository.findByName(userName);
    }

    // 指定した年月のタスクを取得（ユーザー用）
    public List<Task> getTasksByMonthAndUser(LocalDate startDate, LocalDate endDate, String userName) {
        return taskRepository.findTasksByMonthAndUser(startDate, endDate, userName);
    }

    // 指定した年月のタスクを取得（管理者用）
    public List<Task> getTasksByMonthForAdmin(LocalDate startDate, LocalDate endDate) {
        return taskRepository.findTasksByMonthForAdmin(startDate, endDate);
    }
}
