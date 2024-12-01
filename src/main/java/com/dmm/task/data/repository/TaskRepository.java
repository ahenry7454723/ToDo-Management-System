package com.dmm.task.data.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dmm.task.data.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByName(String userName);

    // 指定した年月のタスクを取得
    @Query("SELECT t FROM Task t WHERE t.date >= :startDate AND t.date <= :endDate AND t.name = :userName")
    List<Task> findTasksByMonthAndUser(@Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate, 
                                       @Param("userName") String userName);

    @Query("SELECT t FROM Task t WHERE t.date >= :startDate AND t.date <= :endDate")
    List<Task> findTasksByMonthForAdmin(@Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);
}
