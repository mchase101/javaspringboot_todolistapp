package com.example.todoapp.controller;

import com.example.todoapp.model.Task;
import com.example.todoapp.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    private static final String API_ENDPOINT = "/api/v1/tasks";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task1;
    private Task task2;
    private Task task3;

    private final List<Task> taskList = new ArrayList<>();

    @BeforeEach
    void setUp() {

        // delete all task records before each test
        taskRepository.deleteAll();
        // clear the list so that each test starts with fresh test data.
        taskList.clear();

        // arrange - precondition
        task1 = new Task("Submit homework", false);
        task2 = new Task("Buy groceries", true);
        task3 = new Task("Read Spring Boot notes", false);

        taskList.add(task1);
        taskList.add(task2);
        taskList.add(task3);
    }
    // Clean after the test finish
    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
    }

    // Create Task()
    @Test
    @DisplayName("**JUNIT test: create a task via TaskController**")
    void createTask() throws Exception {

        // arrange - prepare
        Task newTask = new Task("Learn JUnit", false);
        String requestBody = objectMapper.writeValueAsString(newTask);

        // act - simulate POST /api/v1/tasks/
        ResultActions resultActions = mockMvc.perform(post(API_ENDPOINT.concat("/"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // assert - verify the output, check HTTP status and JSON response
        resultActions.andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.task").value(newTask.getTask()))
                .andExpect(jsonPath("$.completed").value(newTask.isCompleted()));
    }

    // getAllTasks()
    @Test
    @DisplayName("**JUNIT test: get all tasks via TaskController**")
    void getAllTasks() throws Exception {

        // arrange - prepare
        taskRepository.saveAll(taskList);

        // act - action, simulate GET /api/v1/tasks/
        ResultActions resultActions = mockMvc.perform(get(API_ENDPOINT.concat("/")));

        // assert - verify the output has 3 tasks
        // containsInAnyOrder is used to check that these tasks value exist, but do not require a spesific order
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()").value(taskList.size()))
                .andExpect(jsonPath("$[*].task", containsInAnyOrder(
                        task1.getTask(),
                        task2.getTask(),
                        task3.getTask()
                )));
    }

    // getAllCompletedTasks()
    @Test
    @DisplayName("**JUNIT test: get all completed tasks via TaskController**")
    void getAllCompletedTasks() throws Exception {

        // arrange - prepare
        taskRepository.saveAll(taskList);

        // act - action GET /api/v1/tasks/completed
        ResultActions resultActions = mockMvc.perform(get(API_ENDPOINT.concat("/completed")));

        // assert - verify result only the completed task should be returned
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].task").value(task2.getTask()))
                .andExpect(jsonPath("$[0].completed").value(true));
    }

    // getAllIncompletedTasks()
    @Test
    @DisplayName("**JUNIT test: get all incomplete tasks via TaskController**")
    void getAllIncompleteTasks() throws Exception {

        // arrange - prepare, save both completed and incomplete tasks
        taskRepository.saveAll(taskList);

        // act - action GET /api/v1/tasks/incomplete
        ResultActions resultActions = mockMvc.perform(get(API_ENDPOINT.concat("/incomplete")));

        // assert - verify only the incomplete tasks should be returned
        // added everyItem to check that every returned task has completed = false together with is
        // completed values = [false, false, false] is good
        //completed values = [false, true, false] is fail
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[*].task", containsInAnyOrder(
                        task1.getTask(),
                        task3.getTask()
                )))
                .andExpect(jsonPath("$[*].completed", everyItem(is(false))));
    }

    // updateTask()
    @Test
    @DisplayName("**JUNIT test: update a task via TaskController**")
    void updateTask() throws Exception {

        // arrange - preapare, save an existing task first
        Task savedTask = taskRepository.save(task1);

        // prepare updated task data
        Task updatedTask = new Task("Submit updated homework", true);
        String requestBody = objectMapper.writeValueAsString(updatedTask);

        // act - action PUT /api/v1/tasks/{id}
        ResultActions resultActions = mockMvc.perform(put(API_ENDPOINT.concat("/{id}"), savedTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // assert - verify the updated response
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(savedTask.getId().intValue()))
                .andExpect(jsonPath("$.task").value(updatedTask.getTask()))
                .andExpect(jsonPath("$.completed").value(updatedTask.isCompleted()));
    }

    // deleteTask()
    @Test
    @DisplayName("**JUNIT test: delete a task by ID via TaskController**")
    void deleteTask() throws Exception {

        // arrange - preapre
        Task savedTask = taskRepository.save(task2);

        // act - action DELETE /api/v1/tasks/{id}
        ResultActions resultActions = mockMvc.perform(delete(API_ENDPOINT.concat("/{id}"), savedTask.getId()));

        // assert - verify result deleted task is returned in the response
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(savedTask.getId().intValue()))
                .andExpect(jsonPath("$.task").value(savedTask.getTask()))
                .andExpect(jsonPath("$.completed").value(savedTask.isCompleted()));

        // extra database check - confirm it was really deleted
        assertFalse(taskRepository.existsById(savedTask.getId()));
    }
}