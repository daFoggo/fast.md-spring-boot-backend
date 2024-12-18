package com.fastmd.backend.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fastmd.backend.domain.entity.Tag;
import com.fastmd.backend.domain.entity.User;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByNameAndUser(String name, User user);

    Page<Tag> findByUser(User user, Pageable pageable);

    List<Tag> findByNameContainingAndUser(String keyword, User user);
}