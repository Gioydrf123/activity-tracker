package com.example.activitytracker.repository;

import com.example.activitytracker.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByTaskIdOrderByCreatedAtDesc(Long taskId);
}
