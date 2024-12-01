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

    /**
     * タスクを保存する
     * 
     * @param task 保存するタスク
     */
    public void saveTask(Task task) {
        taskRepository.save(task);
    }

    /**
     * IDでタスクを検索する
     * 
     * @param id タスクID
     * @return タスクが見つかれば返却、見つからなければnull
     */
    public Task findById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    /**
     * IDでタスクを削除する
     * 
     * @param id タスクID
     */
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    /**
     * 指定範囲内のタスクを取得する（全ユーザー）
     * 
     * @param startDate 開始日
     * @param endDate   終了日
     * @return タスクリスト
     */
    public List<Task> getTasksByDateRange(LocalDate startDate, LocalDate endDate) {
        return taskRepository.findTasksByDateRange(startDate, endDate);
    }

    /**
     * 指定範囲内の特定ユーザーのタスクを取得する
     * 
     * @param startDate 開始日
     * @param endDate   終了日
     * @param userName  ユーザー名
     * @return タスクリスト
     */
    public List<Task> getTasksByDateRangeAndUser(LocalDate startDate, LocalDate endDate, String userName) {
        return taskRepository.findTasksByDateRangeAndUser(startDate, endDate, userName);
    }
}
