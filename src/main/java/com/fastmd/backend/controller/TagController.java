package com.fastmd.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastmd.backend.domain.entity.User;
import com.fastmd.backend.dto.request.TagRequest;
import com.fastmd.backend.dto.response.TagResponse;
import com.fastmd.backend.service.TagService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @PostMapping
    public ResponseEntity<TagResponse> createTag(
            @Valid @RequestBody TagRequest request,
            @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(tagService.createTag(request, user), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<TagResponse>> getAllTags(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(tagService.getAllTags(user, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponse> getTagById(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(tagService.getTagById(id, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponse> updateTag(
            @PathVariable String id,
            @Valid @RequestBody TagRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(tagService.updateTag(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
        tagService.deleteTag(id, user);
        return ResponseEntity.noContent().build();
    }
}