package io.github.dkuechler.jbench.repositories;

import io.github.dkuechler.jbench.model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    
    List<QuizQuestion> findByCategory(String category);
    
    @Query("SELECT q FROM QuizQuestion q WHERE LOWER(q.question) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<QuizQuestion> findByQuestionContaining(@Param("searchText") String searchText);
    
    @Query("SELECT DISTINCT q.category FROM QuizQuestion q ORDER BY q.category")
    List<String> findAllCategories();
}