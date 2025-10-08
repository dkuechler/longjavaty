package io.github.dkuechler.jbench.services;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import io.github.dkuechler.jbench.model.QuizQuestion;
import io.github.dkuechler.jbench.model.QuizResult;

@Service
public class QuizService {
    // TODO replace with a database
    private List<QuizQuestion> questions = new ArrayList<>();
    private Long nextId = 1L;

    @PostConstruct
    public void initializeQuestions() {
        // debugging questions
        addQuestion(new QuizQuestion("What is the capital of France?", 
            List.of("London", "New York", "Paris", "Madrid"), 2, "Geography"));

        addQuestion(new QuizQuestion("What is the capital of Germany?", 
            List.of("Munich", "Berlin"), 1, "Geography"));
        
        addQuestion(new QuizQuestion("What is 2 + 2?", 
            List.of("3", "4"), 1, "Math"));
    }

    public QuizQuestion addQuestion(QuizQuestion question) {
        QuizQuestion questionWithId = new QuizQuestion(
            question.getQuestion(),
            question.getOptions(),
            question.getCorrectAnswerIndex(),
            question.getCategory()
        );
        questionWithId.setId(nextId++);
        questions.add(questionWithId);
        return questionWithId;
    }

    public List<QuizQuestion> getAllQuestions() {
        return List.copyOf(questions);
    }

    public Optional<QuizQuestion> getQuestionById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return questions.stream()
            .filter(q -> q.getId() != null && q.getId().equals(id))
            .findFirst();
    }

    public List<QuizQuestion> getQuestionsByCategory(String category) {
        return questions.stream()
            .filter(q -> q.getCategory().equalsIgnoreCase(category))
            .toList();
    }
    
    public QuizResult checkAnswer(Long questionId, String userAnswer) {
        Optional<QuizQuestion> questionOpt = getQuestionById(questionId);
        if (questionOpt.isEmpty()) {
            return null;
        }
        
        QuizQuestion question = questionOpt.get();
        String correctAnswer = question.getOptions().get(question.getCorrectAnswerIndex());
        boolean isCorrect = correctAnswer.equalsIgnoreCase(userAnswer);
        
        return new QuizResult(questionId, question.getQuestion(), userAnswer, correctAnswer, isCorrect);
    }
    
    public int calculateScore(List<QuizResult> results) {
        return (int) results.stream().mapToInt(r -> r.correct() ? 1 : 0).sum();
    }
}