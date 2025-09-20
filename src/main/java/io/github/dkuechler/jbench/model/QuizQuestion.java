package io.github.dkuechler.jbench.model;

import java.util.List;

public record QuizQuestion(
    Long id,
    String question,
    List<String> options,
    int correctAnswerIndex,
    String category
) {
}