package com.electionmonitor.controller;

import com.electionmonitor.dto.DiscussionDTO;
import com.electionmonitor.service.DiscussionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discussions")
@RequiredArgsConstructor
@Tag(name = "Civic Discussions", description = "Endpoints for civic engagement and community discussions")
public class DiscussionController {

    private final DiscussionService discussionService;

    @GetMapping
    @Operation(summary = "Get all discussions", description = "Retrieves all discussion threads ordered by date")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    public ResponseEntity<List<DiscussionDTO>> getAllDiscussions() {
        return ResponseEntity.ok(discussionService.getAllDiscussions());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get discussion by ID", description = "Retrieves a specific discussion thread with all comments")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved discussion")
    @ApiResponse(responseCode = "404", description = "Discussion not found")
    public ResponseEntity<DiscussionDTO> getDiscussionById(@PathVariable Long id) {
        return ResponseEntity.ok(discussionService.getDiscussionById(id));
    }

    @PostMapping
    @Operation(summary = "Create a discussion", description = "Starts a new discussion thread")
    @ApiResponse(responseCode = "200", description = "Successfully created")
    public ResponseEntity<DiscussionDTO> createDiscussion(
            @Valid @RequestBody DiscussionDTO dto,
            Authentication authentication) {
        return ResponseEntity.ok(discussionService.createDiscussion(dto, authentication.getName()));
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Add a comment", description = "Adds a comment to an existing discussion thread")
    @ApiResponse(responseCode = "200", description = "Successfully added comment")
    public ResponseEntity<DiscussionDTO> addComment(
            @PathVariable Long id,
            @Valid @RequestBody DiscussionDTO.CommentDTO commentDTO,
            Authentication authentication) {
        return ResponseEntity.ok(discussionService.addComment(id, commentDTO, authentication.getName()));
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "Like a discussion", description = "Increments the like count of a discussion thread")
    @ApiResponse(responseCode = "200", description = "Successfully liked")
    public ResponseEntity<DiscussionDTO> likeDiscussion(@PathVariable Long id) {
        return ResponseEntity.ok(discussionService.likeDiscussion(id));
    }
}
