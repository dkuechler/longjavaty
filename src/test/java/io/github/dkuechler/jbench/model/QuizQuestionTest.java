package io.github.dkuechler.jbench.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuizQuestionTest {

    @Test
    void shouldCreateQuestionWithValidParameters() {
        String question = "What is the capital of France?";
        List<String> options = List.of("London", "Paris", "Madrid");
        int correctAnswerIndex = 1;
        String category = "Geography";

        QuizQuestion quizQuestion = new QuizQuestion(question, options, correctAnswerIndex, category);

        assertEquals(question, quizQuestion.getQuestion());
        assertEquals(options, quizQuestion.getOptions());
        assertEquals(correctAnswerIndex, quizQuestion.getCorrectAnswerIndex());
        assertEquals(category, quizQuestion.getCategory());
        assertNull(quizQuestion.getId());
    }

    @Test
    void shouldGetAndSetId() {
        QuizQuestion quizQuestion = new QuizQuestion("Test", List.of("A", "B"), 0, "Test");
        
        quizQuestion.setId(42L);

        assertEquals(42L, quizQuestion.getId());
    }

    @Test
    void shouldGetAndSetQuestionText() {
        QuizQuestion quizQuestion = new QuizQuestion("Test", List.of("A", "B"), 0, "Test");
        String newQuestion = "Updated question text?";

        quizQuestion.setQuestion(newQuestion);

        assertEquals(newQuestion, quizQuestion.getQuestion());
    }

    @Test
    void shouldGetAndSetOptions() {
        QuizQuestion quizQuestion = new QuizQuestion("Test", List.of("A", "B"), 0, "Test");
        List<String> newOptions = List.of("New A", "New B", "New C");

        quizQuestion.setOptions(newOptions);

        assertEquals(newOptions, quizQuestion.getOptions());
        assertEquals(3, quizQuestion.getOptions().size());
    }

    @Test
    void shouldGetAndSetCorrectAnswerIndex() {
        QuizQuestion quizQuestion = new QuizQuestion("Test", List.of("A", "B"), 0, "Test");

        quizQuestion.setCorrectAnswerIndex(1);

        assertEquals(1, quizQuestion.getCorrectAnswerIndex());
    }

    @Test
    void shouldGetAndSetCategory() {
        QuizQuestion quizQuestion = new QuizQuestion("Test", List.of("A", "B"), 0, "Test");
        String newCategory = "Science";

        quizQuestion.setCategory(newCategory);

        assertEquals(newCategory, quizQuestion.getCategory());
    }
}