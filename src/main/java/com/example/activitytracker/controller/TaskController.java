package com.example.activitytracker.controller;

import com.example.activitytracker.dto.*;
import com.example.activitytracker.entity.Task;
import com.example.activitytracker.entity.TaskStatus;
import com.example.activitytracker.entity.User;
import com.example.activitytracker.repository.UserRepository;
import com.example.activitytracker.service.TaskService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

	private final TaskService taskService;
	private final UserRepository userRepository;

	// Iniezione via costruttore (consigliata)
	public TaskController(TaskService taskService, UserRepository userRepository) {
		this.taskService = taskService;
		this.userRepository = userRepository;
	}
	
	// Recupera i task dell'utente loggato, eventualmente filtrati per stato.
	
	@GetMapping
	public ResponseEntity<List<TaskSummaryDto>> getMyTasks(@RequestParam(required = false) TaskStatus status, Authentication auth) {
		User user = getCurrentUser(auth);
		return ResponseEntity.ok(taskService.getTasksForUserByStatus(user, status));
	}
	
	// Recupera il dettaglio di un singolo task.
	
	@GetMapping("/{id}")
	public ResponseEntity<TaskDetailDto> getTaskDetail(@PathVariable Long id) {
		return ResponseEntity.ok(taskService.getTaskDetail(id));
	}
	
	// Crea un nuovo task (lo assegna automaticamente al creatore).
	
	@PostMapping
	public ResponseEntity<TaskDetailDto> createTask(@RequestBody CreateTaskRequest req, Authentication auth) {
		User creator = getCurrentUser(auth);
		Task task = taskService.createTask(req.getTitle(), req.getDescription(), creator);
		return ResponseEntity.status(HttpStatus.CREATED).body(taskService.getTaskDetail(task.getId()));
	}
	
	// Cambia lo stato di un task.
	
	@PutMapping("/{id}/status")
	public ResponseEntity<Void> changeStatus(@PathVariable Long id, @RequestBody ChangeStatusRequest req) {
		taskService.changeStatus(id, req.getStatus());
		return ResponseEntity.ok().build();
	}
	
	// Assegna un utente a un task.
	
	@PostMapping("/{id}/assignees")
	public ResponseEntity<Void> addAssignee(@PathVariable Long id, @RequestBody AddAssigneeRequest req) {
		taskService.assignUserToTask(id, req.getUserId());
		return ResponseEntity.ok().build();
	}
	
	// Registra ore lavorate su un task.
	
	@PostMapping("/{id}/worklogs")
	public ResponseEntity<Void> logWork(@PathVariable Long id, @RequestBody LogWorkRequest req, Authentication auth) {
		User user = getCurrentUser(auth);
		taskService.logWork(id, req.getHours(), user);
		return ResponseEntity.ok().build();
	}
	
	// Aggiunge un commento a un task.
	
	@PostMapping("/{id}/comments")
	public ResponseEntity<Void> addComment(@PathVariable Long id, @RequestBody AddCommentRequest req, Authentication auth) {
		User user = getCurrentUser(auth);
		taskService.addComment(id, req.getText(), user);
		return ResponseEntity.ok().build();
	}
	
	// Estrae l'utente corrente dall'Authentication di Spring Security.
	
	private User getCurrentUser(Authentication auth) {
	String username = auth.getName();

	return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Utente non trovato: " + username));
	}
}
