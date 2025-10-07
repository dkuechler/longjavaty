package io.github.dkuechler.jbench.services;

import io.github.dkuechler.jbench.model.QuizQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class QuizServiceTest {

    private QuizService quizService;

    @BeforeEach
    void setUp() {
        quizService = new QuizService();
        quizService.initializeQuestions();
    }

    @Test
    void shouldAddValidQuestion() {
        QuizQuestion newQuestion = new QuizQuestion(
            "What is the capital of Italy?",
            List.of("Rome", "Milan", "Naples"),
            0,
            "Geography"
        );

        QuizQuestion addedQuestion = quizService.addQuestion(newQuestion);

        assertNotNull(addedQuestion);
        assertEquals("What is the capital of Italy?", addedQuestion.getQuestion());
        assertEquals(3, addedQuestion.getOptions().size());
        assertEquals(0, addedQuestion.getCorrectAnswerIndex());
        assertEquals("Geography", addedQuestion.getCategory());
    }

    @Test
    void shouldReturnAllQuestions() {
        List<QuizQuestion> questions = quizService.getAllQuestions();

        assertNotNull(questions);
        assertEquals(3, questions.size());
    }

    @Test
    void shouldReturnQuestionWhenIdExists() {
        List<QuizQuestion> allQuestions = quizService.getAllQuestions();
        QuizQuestion firstQuestion = allQuestions.get(0);

        Optional<QuizQuestion> result = quizService.getQuestionById(firstQuestion.getId());

        assertTrue(result.isPresent());
        assertEquals(firstQuestion.getQuestion(), result.get().getQuestion());
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        Optional<QuizQuestion> result = quizService.getQuestionById(99999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnQuestionsForExistingCategory() {
        List<QuizQuestion> geographyQuestions = quizService.getQuestionsByCategory("Geography");

        assertNotNull(geographyQuestions);
        assertEquals(2, geographyQuestions.size());
    }

    @Test
    void shouldBeCaseInsensitive() {
        List<QuizQuestion> upperCase = quizService.getQuestionsByCategory("GEOGRAPHY");
        List<QuizQuestion> lowerCase = quizService.getQuestionsByCategory("geography");

        assertEquals(2, upperCase.size());
        assertEquals(2, lowerCase.size());
    }
}