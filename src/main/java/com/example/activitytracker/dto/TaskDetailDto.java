package com.example.activitytracker.dto;

import java.util.List;

public class TaskDetailDto extends TaskSummaryDto {

	private String description;
	private String createdByUsername;
	private List<CommentDto> comments;
	
	public TaskDetailDto(
		Long id,
		String title,
		String status,
		String statusColor,
		Double totalHours,
		List<String> assigneeNames,
		String description,
		String createdByUsername,
		List<CommentDto> comments
	) {
		super(id, title, status, statusColor, totalHours, assigneeNames);
		this.description = description;
		this.createdByUsername = createdByUsername;
		this.comments = comments;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getCreatedByUsername() {
		return createdByUsername;
	}
	
	public List<CommentDto> getComments() {
		return comments;
	}
}
