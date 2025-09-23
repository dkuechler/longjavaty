package io.github.dkuechler.jbench.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "quiz_questions")
public class QuizQuestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;
    
    @ElementCollection
    @CollectionTable(name = "quiz_question_options", joinColumns = @JoinColumn(name = "quiz_question_id"))
    @Column(name = "option_text")
    private List<String> options;
    
    @Column(name = "correct_answer_index", nullable = false)
    private int correctAnswerIndex;
    
    @Column(nullable = false, length = 100)
    private String category;
    
    // Default constructor for JPA
    protected QuizQuestion() {}
    
    // Business constructor (for new entities)
    public QuizQuestion(String question, List<String> options, int correctAnswerIndex, String category) {
        this.question = question;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.category = category;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    
    public int getCorrectAnswerIndex() { return correctAnswerIndex; }
    public void setCorrectAnswerIndex(int correctAnswerIndex) { this.correctAnswerIndex = correctAnswerIndex; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}