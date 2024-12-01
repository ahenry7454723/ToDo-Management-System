package com.dmm.task.data.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dmm.task.data.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // 指定範囲のタスクを取得
    @Query("SELECT t FROM Task t WHERE t.date BETWEEN :startDate AND :endDate")
    List<Task> findTasksByDateRange(@Param("startDate") LocalDate startDate, 
                                    @Param("endDate") LocalDate endDate);

    // 指定範囲とユーザー名で絞り込んだタスクを取得
    @Query("SELECT t FROM Task t WHERE t.date BETWEEN :startDate AND :endDate AND t.name = :userName")
    List<Task> findTasksByDateRangeAndUser(@Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate, 
                                           @Param("userName") String userName);
}
