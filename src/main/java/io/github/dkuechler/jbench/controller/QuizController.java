package io.github.dkuechler.jbench.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.dkuechler.jbench.model.QuizQuestion;
import io.github.dkuechler.jbench.services.QuizService;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/questions")
    public ResponseEntity<List<QuizQuestion>> getAllQuestions() {
        List<QuizQuestion> questions = quizService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<QuizQuestion> getQuestionById(@PathVariable Long id) {
        return quizService.getQuestionById(id)
            .map(question -> ResponseEntity.ok(question))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/questions/category/{category}")
    public ResponseEntity<List<QuizQuestion>> getQuestionsByCategory(@PathVariable String category) {
        List<QuizQuestion> questions = quizService.getQuestionsByCategory(category);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/questions")
    public ResponseEntity<QuizQuestion> addQuestion(@RequestBody QuizQuestion question) {
        QuizQuestion createdQuestion = quizService.addQuestion(question);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
    }
}