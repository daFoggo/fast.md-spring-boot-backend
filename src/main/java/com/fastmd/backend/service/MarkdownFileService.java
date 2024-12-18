package com.fastmd.backend.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastmd.backend.domain.entity.MarkdownFile;
import com.fastmd.backend.domain.entity.Tag;
import com.fastmd.backend.domain.entity.User;
import com.fastmd.backend.domain.repository.MarkdownFileRepository;
import com.fastmd.backend.domain.repository.TagRepository;
import com.fastmd.backend.dto.request.MarkdownFileRequest;
import com.fastmd.backend.dto.response.MarkdownFileResponse;
import com.fastmd.backend.dto.response.TagResponse;
import com.fastmd.backend.exception.ResourceNotFoundException;
import com.fastmd.backend.exception.UnauthorizedAccessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarkdownFileService {
    private final MarkdownFileRepository markdownFileRepository;
    private final TagRepository tagRepository;

    @Transactional
    public MarkdownFileResponse createMarkdownFile(MarkdownFileRequest request, User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        MarkdownFile markdownFile = new MarkdownFile();
        markdownFile.setTitle(request.getTitle());
        markdownFile.setContent(request.getContent());
        markdownFile.setUser(user);


        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = request.getTagIds().stream()
                    .map(tagId -> tagRepository.findById(tagId)
                            .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + tagId)))
                    .collect(Collectors.toSet());
            markdownFile.setTags(tags);
        }

        return convertToResponse(markdownFileRepository.save(markdownFile));
    }

    @Transactional(readOnly = true)
    public Page<MarkdownFileResponse> getAllMarkdownFiles(User user, Pageable pageable) {
        return markdownFileRepository.findByUser(user, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Page<MarkdownFileResponse> searchMarkdownFiles(String keyword, User user, Pageable pageable) {
        return markdownFileRepository.findByTitleContainingAndUser(keyword, user, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public MarkdownFileResponse getMarkdownFileById(Long id, User user) {
        MarkdownFile markdownFile = findFileAndCheckOwnership(id, user);
        return convertToResponse(markdownFile);
    }

    @Transactional
    public MarkdownFileResponse updateMarkdownFile(Long id, MarkdownFileRequest request, User user) {
        MarkdownFile markdownFile = findFileAndCheckOwnership(id, user);

        markdownFile.setTitle(request.getTitle());
        markdownFile.setContent(request.getContent());

        if (request.getTagIds() != null) {
            Set<Tag> tags = request.getTagIds().stream()
                    .map(tagId -> tagRepository.findById(tagId)
                            .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + tagId)))
                    .collect(Collectors.toSet());
            markdownFile.setTags(tags);
        }

        return convertToResponse(markdownFileRepository.save(markdownFile));
    }

    @Transactional
    public void deleteMarkdownFile(Long id, User user) {
        MarkdownFile markdownFile = findFileAndCheckOwnership(id, user);
        markdownFileRepository.delete(markdownFile);
    }

    private MarkdownFile findFileAndCheckOwnership(Long id, User user) {
        MarkdownFile markdownFile = markdownFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy file với id: " + id));

        if (!markdownFile.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("Bạn không có quyền truy cập file này");
        }

        return markdownFile;
    }

    private MarkdownFileResponse convertToResponse(MarkdownFile markdownFile) {
        MarkdownFileResponse response = new MarkdownFileResponse();
        response.setId(markdownFile.getId());
        response.setTitle(markdownFile.getTitle());
        response.setContent(markdownFile.getContent());
        response.setUserId(markdownFile.getUser().getId().toString());
        response.setCreatedAt(markdownFile.getCreatedAt());
        response.setUpdatedAt(markdownFile.getUpdatedAt());

        Set<TagResponse> tagResponses = markdownFile.getTags().stream()
                .map(tag -> {
                    TagResponse tagResponse = new TagResponse();
                    tagResponse.setId(tag.getId());
                    tagResponse.setName(tag.getName());
                    tagResponse.setDescription(tag.getDescription());
                    return tagResponse;
                })
                .collect(Collectors.toSet());
        response.setTags(tagResponses);

        return response;
    }
}