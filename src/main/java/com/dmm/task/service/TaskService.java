package com.dmm.task.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dmm.task.data.entity.Task;
import com.dmm.task.data.repository.TaskRepository;

@Service
public class TaskService {

	@Autowired
	private TaskRepository taskRepository;

	// タスクを保存する
	public void saveTask(Task task) {
		taskRepository.save(task);
	}

	// IDでタスクを取得する
	public Task findById(Long id) {
		return taskRepository.findById(id).orElse(null);
	}

	// タスクを削除する
	public void deleteTask(Long id) {
		taskRepository.deleteById(id);
	}

	// ユーザー名でタスクを取得する
	public List<Task> getTasksByUser(String userName) {
		return taskRepository.findByName(userName);
	}

	public void updateTask(Task task) {
		// TODO 自動生成されたメソッド・スタブ

	}
}
