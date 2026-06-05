package com.example.activitytracker.repository;

import com.example.activitytracker.entity.Task;
import com.example.activitytracker.entity.TaskStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {
	@Query("SELECT t FROM Task t JOIN t.assignees a WHERE a.user.id = :userId AND t.status = :status")
	List<Task> findByAssigneeAndStatus(@Param("userId") Long userId, @Param("status") TaskStatus status);

	@Query("SELECT t FROM Task t JOIN t.assignees a WHERE a.user.id = :userId")
	List<Task> findByAssignee(@Param("userId") Long userId);
}
