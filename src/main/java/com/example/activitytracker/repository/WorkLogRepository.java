package com.example.activitytracker.repository;

import com.example.activitytracker.entity.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
	@Query("SELECT COALESCE(SUM(w.hours), 0) FROM WorkLog w WHERE w.task.id = :taskId")
	Double getTotalHoursByTask(@Param("taskId") Long taskId);
}
