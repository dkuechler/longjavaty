package io.github.dkuechler.jbench.services;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.dkuechler.jbench.model.QuizQuestion;

@Service
public class QuizService {
    // TODO replace with a database
    private List<QuizQuestion> questions = new ArrayList<>();
    private Long nextId = 1L;

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