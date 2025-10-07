package io.github.dkuechler.jbench.repositories;

import io.github.dkuechler.jbench.model.QuizQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class QuizQuestionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuizQuestionRepository repository;

    private QuizQuestion geographyQuestion;

    @BeforeEach
    void setUp() {
        geographyQuestion = new QuizQuestion(
            "What is the capital of France?",
            List.of("London", "Paris", "Madrid"),
            1,
            "Geography"
        );
        
        entityManager.persistAndFlush(geographyQuestion);
    }

    @Test
    void shouldSaveAndRetrieveQuestion() {
        QuizQuestion newQuestion = new QuizQuestion(
            "What is 2 + 2?",
            List.of("3", "4", "5"),
            1,
            "Math"
        );

        QuizQuestion saved = repository.save(newQuestion);
        Optional<QuizQuestion> retrieved = repository.findById(saved.getId());

        assertTrue(retrieved.isPresent());
        assertEquals(newQuestion.getQuestion(), retrieved.get().getQuestion());
        assertNotNull(retrieved.get().getId());
    }

    @Test
    void shouldFindQuestionsByCategory() {
        List<QuizQuestion> geographyQuestions = repository.findByCategory("Geography");

        assertEquals(1, geographyQuestions.size());
        assertEquals("Geography", geographyQuestions.get(0).getCategory());
    }

    @Test
    void shouldFindQuestionsByTextSearch() {
        List<QuizQuestion> capitalQuestions = repository.findByQuestionContaining("capital");

        assertEquals(1, capitalQuestions.size());
        assertTrue(capitalQuestions.get(0).getQuestion().toLowerCase().contains("capital"));
    }

    @Test
    void shouldReturnAllCategories() {
        List<String> categories = repository.findAllCategories();

        assertEquals(1, categories.size());
        assertTrue(categories.contains("Geography"));
    }
}