package io.github.dkuechler.jbench.controller;

import io.github.dkuechler.jbench.model.QuizQuestion;
import io.github.dkuechler.jbench.services.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(QuizController.class)
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuizService quizService;

    private List<QuizQuestion> sampleQuestions;
    private QuizQuestion sampleQuestion;

    @BeforeEach
    void setUp() {
        sampleQuestion = new QuizQuestion(
            "What is the capital of France?",
            List.of("London", "New York", "Paris", "Madrid"),
            2,
            "Geography"
        );
        
        sampleQuestions = List.of(sampleQuestion);
    }

    @Test
    void shouldReturnAllQuestions() throws Exception {
        when(quizService.getAllQuestions()).thenReturn(sampleQuestions);

        mockMvc.perform(get("/api/quiz/questions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].question", is("What is the capital of France?")));

        verify(quizService, times(1)).getAllQuestions();
    }

    @Test
    void shouldReturnQuestionById() throws Exception {
        Long questionId = 1L;
        when(quizService.getQuestionById(questionId)).thenReturn(Optional.of(sampleQuestion));

        mockMvc.perform(get("/api/quiz/questions/{id}", questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question", is("What is the capital of France?")))
                .andExpect(jsonPath("$.category", is("Geography")));

        verify(quizService, times(1)).getQuestionById(questionId);
    }

    @Test
    void shouldReturn404WhenQuestionNotFound() throws Exception {
        Long nonExistentId = 999L;
        when(quizService.getQuestionById(nonExistentId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/quiz/questions/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnQuestionsByCategory() throws Exception {
        String category = "Geography";
        when(quizService.getQuestionsByCategory(category)).thenReturn(sampleQuestions);

        mockMvc.perform(get("/api/quiz/questions/category/{category}", category))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category", is("Geography")));

        verify(quizService, times(1)).getQuestionsByCategory(category);
    }
}