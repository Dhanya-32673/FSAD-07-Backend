package com.electionmonitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO representing a Civic Discussion thread")
public class DiscussionDTO {
    @Schema(description = "Unique identifier of the discussion", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Schema(description = "Title of the discussion", example = "Thoughts on the new electoral reforms?")
    private String title;

    @NotBlank(message = "Content is required")
    @Schema(description = "Main content or body of the discussion", example = "What do you all think about the recently proposed changes...")
    private String content;

    @Schema(description = "ID of the related election", example = "1")
    private Long electionId;

    @Schema(description = "Title of the related election", accessMode = Schema.AccessMode.READ_ONLY)
    private String electionTitle;

    @Schema(description = "Name of the discussion author", accessMode = Schema.AccessMode.READ_ONLY)
    private String authorName;

    @Schema(description = "ID of the discussion author", accessMode = Schema.AccessMode.READ_ONLY)
    private Long authorId;

    @Schema(description = "Number of likes/upvotes", example = "10")
    private int likes;

    @Schema(description = "Timestamp when created", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "List of comments on this discussion")
    private List<CommentDTO> comments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "DTO representing a Comment on a discussion")
    public static class CommentDTO {
        @Schema(description = "Unique identifier of the comment", example = "5", accessMode = Schema.AccessMode.READ_ONLY)
        private Long id;

        @NotBlank(message = "Content is required")
        @Schema(description = "Content of the comment", example = "I think it's a step in the right direction.")
        private String content;

        @Schema(description = "Name of the comment author", accessMode = Schema.AccessMode.READ_ONLY)
        private String authorName;

        @Schema(description = "ID of the comment author", accessMode = Schema.AccessMode.READ_ONLY)
        private Long authorId;

        @Schema(description = "Timestamp when created", accessMode = Schema.AccessMode.READ_ONLY)
        private LocalDateTime createdAt;
    }
}
