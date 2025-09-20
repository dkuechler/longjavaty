package io.github.dkuechler.jbench.services;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import io.github.dkuechler.jbench.model.QuizQuestion;

@Service
public class QuizService {
    // TODO replace with a database
    private List<QuizQuestion> questions = new ArrayList<>();
    private Long nextId = 1L;

    @PostConstruct
    public void initializeQuestions() {
        // debugging questions
        addQuestion(new QuizQuestion(null, "What is the capital of France?", 
            List.of("London", "New York", "Paris", "Madrid"), 2, "Geography"));

        addQuestion(new QuizQuestion(Long.valueOf(42), "What is the capital of Germany?", 
            List.of("Munich", "Berlin"), 2, "Geography"));
        
        addQuestion(new QuizQuestion(Long.valueOf(0), "What is 2 + 2?", 
            List.of("3", "6"), 1, "Math"));
    }

    public QuizQuestion addQuestion(QuizQuestion question) {
        QuizQuestion questionWithId = new QuizQuestion(
            nextId++,
            question.question(),
            question.options(),
            question.correctAnswerIndex(),
            question.category()
        );
        questions.add(questionWithId);
        return questionWithId;
    }

    public List<QuizQuestion> getAllQuestions() {
        return List.copyOf(questions);
    }

    public Optional<QuizQuestion> getQuestionById(Long id) {
        return questions.stream()
            .filter(q -> q.id().equals(id))
            .findFirst();
    }

    public List<QuizQuestion> getQuestionsByCategory(String category) {
        return questions.stream()
            .filter(q -> q.category().equalsIgnoreCase(category))
            .toList();
    }
}