package com.mori.mori.repository;

import com.mori.mori.model.Tutorial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TutorialRepository extends JpaRepository<Tutorial, Long> {
    List<Tutorial> findByPublished(boolean published);
    List<Tutorial> findByTitleContaining(String title);
}


/*
*   List<Tutorial> findByPublished(boolean published);

  List<Tutorial> findByTitleContaining(String title);
* */