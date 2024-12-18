package com.fastmd.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fastmd.backend.domain.entity.User;
import com.fastmd.backend.dto.request.MarkdownFileRequest;
import com.fastmd.backend.dto.response.MarkdownFileResponse;
import com.fastmd.backend.service.MarkdownFileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/markdown")
@RequiredArgsConstructor
public class MarkdownController {
    private final MarkdownFileService markdownFileService;

    @PostMapping
    public ResponseEntity<MarkdownFileResponse> createMarkdownFile(
            @RequestBody MarkdownFileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
                
        User user = (User) userDetails;
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return ResponseEntity.ok(markdownFileService.createMarkdownFile(request, user));
    }

    @GetMapping
    public ResponseEntity<Page<MarkdownFileResponse>> getAllMarkdownFiles(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        return ResponseEntity.ok(markdownFileService.getAllMarkdownFiles(user, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkdownFileResponse> getMarkdownFileById(
            @PathVariable Long id,
            @SuppressWarnings("deprecation") @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(markdownFileService.getMarkdownFileById(id, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkdownFileResponse> updateMarkdownFile(
            @PathVariable Long id,
            @Valid @RequestBody MarkdownFileRequest request,
            @SuppressWarnings("deprecation") @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(markdownFileService.updateMarkdownFile(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMarkdownFile(
            @PathVariable Long id,
            @SuppressWarnings("deprecation") @AuthenticationPrincipal User user) {
        markdownFileService.deleteMarkdownFile(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MarkdownFileResponse>> searchMarkdownFiles(
            @RequestParam String keyword,
            @SuppressWarnings("deprecation") @AuthenticationPrincipal User user,
            Pageable pageable) {
        return ResponseEntity.ok(markdownFileService.searchMarkdownFiles(keyword, user, pageable));
    }
}