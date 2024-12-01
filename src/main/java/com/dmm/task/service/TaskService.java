package com.dmm.task.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmm.task.data.entity.Task;
import com.dmm.task.data.repository.TaskRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    // タスクを保存する
    @Transactional
    public void saveTask(Task task) {
        taskRepository.save(task);
    }

    // IDでタスクを取得する
    public Task findById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    // タスクを削除する
    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    // ユーザー名でタスクを取得する
    public List<Task> getTasksByUser(String userName) {
        return taskRepository.findByName(userName);
    }

    // タスクを更新する（存在する場合のみ保存）
    @Transactional
    public void updateTask(Task task) {
        if (task != null && task.getId() != null && taskRepository.existsById(task.getId())) {
            taskRepository.save(task);
        } else {
            throw new IllegalArgumentException("タスクが存在しないか、IDが設定されていません");
        }
    }

    // 全タスクを取得する
    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }
}
