package io.github.dkuechler.jbench.repositories;

import io.github.dkuechler.jbench.model.QuizQuestion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class QuizQuestionRepositoryJdbc {

    private final JdbcTemplate jdbcTemplate;

    public QuizQuestionRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public QuizQuestion save(QuizQuestion question) {
        String sql = "INSERT INTO quiz_questions (question, options, correct_answer_index, category) VALUES (?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, question.getQuestion());
            ps.setString(2, convertOptionsToJson(question.getOptions()));
            ps.setInt(3, question.getCorrectAnswerIndex());
            ps.setString(4, question.getCategory());
            return ps;
        }, keyHolder);
        
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new RuntimeException("Failed to retrieve generated ID for quiz question");
        }
        Long generatedId = key.longValue();
        
        // Create new question and set the ID
        QuizQuestion savedQuestion = new QuizQuestion(
            question.getQuestion(),
            question.getOptions(),
            question.getCorrectAnswerIndex(),
            question.getCategory()
        );
        savedQuestion.setId(generatedId);
        
        return savedQuestion;
    }

    private String convertOptionsToJson(List<String> options) {
        // Simple JSON array conversion for the options
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < options.size(); i++) {
            json.append("\"").append(options.get(i).replace("\"", "\\\"")).append("\"");
            if (i < options.size() - 1) {
                json.append(", ");
            }
        }
        json.append("]");
        return json.toString();
    }
}