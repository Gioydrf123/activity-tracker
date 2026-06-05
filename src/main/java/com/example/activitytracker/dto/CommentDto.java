package com.example.activitytracker.dto;

import java.time.LocalDateTime;

public class CommentDto {

	private Long id;
	private String text;
	private String authorUsername;
	private LocalDateTime createdAt;
	
	public CommentDto(
		Long id,
		String text,
		String authorUsername,
		LocalDateTime createdAt
	) {
		this.id = id;
		this.text = text;
		this.authorUsername = authorUsername;
		this.createdAt = createdAt;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getText() {
		return text;
	}
	
	public String getAuthorUsername() {
		return authorUsername;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
