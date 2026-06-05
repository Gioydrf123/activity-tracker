package com.example.activitytracker.dto;

import java.util.List;

public class TaskSummaryDto {

	private Long id;
	private String title;
	private String status;
	private String statusColor;
	private Double totalHours;
	private List<String> assigneeNames;
	
	public TaskSummaryDto(
		Long id,
		String title,
		String status,
		String statusColor,
		Double totalHours,
		List<String> assigneeNames
	) {
		this.id = id;
		this.title = title;
		this.status = status;
		this.statusColor = statusColor;
		this.totalHours = totalHours;
		this.assigneeNames = assigneeNames;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getStatusColor() {
		return statusColor;
	}
	
	public Double getTotalHours() {
		return totalHours;
	}
	
	public List<String> getAssigneeNames() {
		return assigneeNames;
	}
}
