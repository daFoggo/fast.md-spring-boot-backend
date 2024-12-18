package com.fastmd.backend.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastmd.backend.domain.entity.Tag;
import com.fastmd.backend.domain.entity.User;
import com.fastmd.backend.domain.repository.TagRepository;
import com.fastmd.backend.dto.request.TagRequest;
import com.fastmd.backend.dto.response.TagResponse;
import com.fastmd.backend.exception.DuplicateResourceException;
import com.fastmd.backend.exception.ResourceNotFoundException;
import com.fastmd.backend.exception.UnauthorizedAccessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    @Transactional
    public TagResponse createTag(TagRequest request, User user) {
        tagRepository.findByNameAndUser(request.getName(), user)
            .ifPresent(t -> {
                throw new DuplicateResourceException("Tag with name " + request.getName() + " already exists");
            });

        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setDescription(request.getDescription());
        tag.setUser(user);
        
        return convertToResponse(tagRepository.save(tag));
    }

    @Transactional(readOnly = true)
    public Page<TagResponse> getAllTags(User user, Pageable pageable) {
        return tagRepository.findByUser(user, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public List<TagResponse> searchTags(String keyword, User user) {
        return tagRepository.findByNameContainingAndUser(keyword, user).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TagResponse getTagById(Long id, User user) {
        Tag tag = findTagAndCheckOwnership(id, user);
        return convertToResponse(tag);
    }

    @Transactional
    public TagResponse updateTag(Long id, TagRequest request, User user) {
        Tag tag = findTagAndCheckOwnership(id, user);
        
        tagRepository.findByNameAndUser(request.getName(), user)
            .ifPresent(t -> {
                if (!t.getId().equals(id)) {
                    throw new DuplicateResourceException("Tag with name " + request.getName() + " already exists");
                }
            });

        tag.setName(request.getName());
        tag.setDescription(request.getDescription());
        
        return convertToResponse(tagRepository.save(tag));
    }

    @Transactional
    public void deleteTag(Long id, User user) {
        Tag tag = findTagAndCheckOwnership(id, user);
        tagRepository.delete(tag);
    }

    @Transactional
    public List<TagResponse> createBulkTags(List<TagRequest> requests, User user) {
        return requests.stream()
                .map(request -> createTag(request, user))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteBulkTags(Set<Long> ids, User user) {
        ids.forEach(id -> deleteTag(id, user));
    }

    private Tag findTagAndCheckOwnership(Long id, User user) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));
        
        if (!tag.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You don't have permission to access this tag");
        }
        
        return tag;
    }

    private TagResponse convertToResponse(Tag tag) {
        TagResponse response = new TagResponse();
        response.setId(tag.getId());
        response.setName(tag.getName());
        response.setDescription(tag.getDescription());
        response.setCreatedAt(tag.getCreatedAt());
        response.setUpdatedAt(tag.getUpdatedAt());
        return response;
    }
}