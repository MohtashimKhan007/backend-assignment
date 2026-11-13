package com.mohtashim.backend_assignment.repository;

import com.mohtashim.backend_assignment.entity.GithubRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GithubRepo extends JpaRepository<GithubRepository, Long> {

    @Query("SELECT r FROM GithubRepository r WHERE " +
            "(:language IS NULL OR LOWER(r.language) = LOWER(CAST(:language AS string))) AND " +
            "(:minStars IS NULL OR r.stars >= :minStars)")
    List<GithubRepository> findFilteredRepositories(
            @Param("language") String language,
            @Param("minStars") Integer minStars,
            Sort sort
    );
}
