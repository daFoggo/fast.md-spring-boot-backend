package com.fastmd.backend.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fastmd.backend.domain.entity.MarkdownFile;
import com.fastmd.backend.domain.entity.User;

@Repository
public interface MarkdownFileRepository extends JpaRepository<MarkdownFile, String> {
    Page<MarkdownFile> findByUser(User user, Pageable pageable);
    Page<MarkdownFile> findByNameContainingAndUser(String keyword, User user, Pageable pageable);
}