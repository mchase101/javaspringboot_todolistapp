package com.example.todoapp.controller;

import com.example.todoapp.exception.ResourceNotFoundException;
import com.example.todoapp.model.Task;
import com.example.todoapp.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // To return all tasks in the database
    @GetMapping("/")
    public ResponseEntity<List<Task>> getAllTasks() throws ResourceNotFoundException {
        List<Task> tasks = taskService.getAllTask();

        if(tasks.isEmpty()){
            throw new ResourceNotFoundException();
        }
        return ResponseEntity.ok(tasks);
    }

    // To return all completed tasks in the database
    @GetMapping("/completed")
    public ResponseEntity<List<Task>> getAllCompletedTasks() {
        List<Task> completedTasks = taskService.findAllCompletedTask();

        if(completedTasks.isEmpty()){
            throw new ResourceNotFoundException("No completed tasks found!");
        }
        return ResponseEntity.ok(completedTasks);
    }

    // To return all incomplete tasks in the database
    @GetMapping("/incomplete")
    public ResponseEntity<List<Task>> getAllIncompleteTasks() {
        List<Task> incompleteTasks = taskService.findAllInCompleteTask();

        if(incompleteTasks.isEmpty()){
            throw new ResourceNotFoundException("No incomplete tasks found!");
        }
        return ResponseEntity.ok(incompleteTasks);
    }

    // To create a new task
    @PostMapping("/")
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task createdTask = taskService.createNewTask(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    // To update an existing task using id
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task task) throws ResourceNotFoundException {
        Task currentTask = taskService.findTaskById(id).map(foundTask -> {

            // update the task
            foundTask.setTask(task.getTask());
            foundTask.setCompleted(task.isCompleted());

            return taskService.updateTask(foundTask);
        }).orElseThrow(() -> new ResourceNotFoundException("Task cannot be found!"));

        return new ResponseEntity<>(currentTask, HttpStatus.OK);
    }

    // To delete a task using id
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTask(@PathVariable Long id) throws ResourceNotFoundException {

        // Delete task only after task is found by id
        Task deletedTask = taskService.findTaskById(id).map(foundTask -> {
            taskService.deleteTask(id);

            return foundTask;
        }).orElseThrow(() -> new ResourceNotFoundException("Task with ID " + id + " cannot be found!"));

        return new ResponseEntity<>(deletedTask, HttpStatus.OK);
    }
}



