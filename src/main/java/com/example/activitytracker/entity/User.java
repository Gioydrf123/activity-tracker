package com.example.activitytracker.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	private String fullName;

	@OneToMany(mappedBy = "createdBy")
	private Set<Task> createdTasks = new HashSet<>();

	@OneToMany(mappedBy = "user")
	private Set<TaskAssignee> assignments = new HashSet<>();

	@OneToMany(mappedBy = "user")
	private Set<WorkLog> workLogs = new HashSet<>();

	@OneToMany(mappedBy = "user")
	private Set<Comment> comments = new HashSet<>();

	public User() {}

	public User(String username, String password, String fullName) {
		this.username = username;
		this.password = password;
		this.fullName = fullName;
	}

	// getters e setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Set<Task> getCreatedTasks() {
		return createdTasks;
	}

	public Set<TaskAssignee> getAssignments() {
		return assignments;
	}

	public Set<WorkLog> getWorkLogs() {
		return workLogs;
	}

	public Set<Comment> getComments() {
		return comments;
	}
}
