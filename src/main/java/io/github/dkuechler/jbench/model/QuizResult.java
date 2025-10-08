package io.github.dkuechler.jbench.model;

public record QuizResult(
    Long questionId,
    String question, 
    String userAnswer,
    String correctAnswer,
    boolean correct
) {}