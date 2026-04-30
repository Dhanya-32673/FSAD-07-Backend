package com.electionmonitor.service;

import com.electionmonitor.dto.DiscussionDTO;
import com.electionmonitor.exception.ResourceNotFoundException;
import com.electionmonitor.model.CivicDiscussion;
import com.electionmonitor.model.DiscussionComment;
import com.electionmonitor.model.Election;
import com.electionmonitor.model.User;
import com.electionmonitor.repository.CommentRepository;
import com.electionmonitor.repository.DiscussionRepository;
import com.electionmonitor.repository.ElectionRepository;
import com.electionmonitor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ElectionRepository electionRepository;
    private final ModelMapper modelMapper;

    public List<DiscussionDTO> getAllDiscussions() {
        return discussionRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DiscussionDTO getDiscussionById(Long id) {
        CivicDiscussion discussion = discussionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found with id: " + id));
        return toDTO(discussion);
    }

    @Transactional
    public DiscussionDTO createDiscussion(DiscussionDTO dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CivicDiscussion discussion = CivicDiscussion.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(user)
                .likes(0)
                .build();

        if (dto.getElectionId() != null) {
            Election election = electionRepository.findById(dto.getElectionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Election not found"));
            discussion.setElection(election);
        }

        discussion = discussionRepository.save(discussion);
        return toDTO(discussion);
    }

    @Transactional
    public DiscussionDTO addComment(Long discussionId, DiscussionDTO.CommentDTO commentDTO, String username) {
        CivicDiscussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        DiscussionComment comment = DiscussionComment.builder()
                .content(commentDTO.getContent())
                .discussion(discussion)
                .author(user)
                .build();

        commentRepository.save(comment);
        return toDTO(discussionRepository.findById(discussionId).get());
    }

    @Transactional
    public DiscussionDTO likeDiscussion(Long id) {
        CivicDiscussion discussion = discussionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));
        discussion.setLikes(discussion.getLikes() + 1);
        discussionRepository.save(discussion);
        return toDTO(discussion);
    }

    private DiscussionDTO toDTO(CivicDiscussion discussion) {
        DiscussionDTO dto = modelMapper.map(discussion, DiscussionDTO.class);
        dto.setAuthorName(discussion.getAuthor().getFullName());
        dto.setAuthorId(discussion.getAuthor().getId());

        if (discussion.getElection() != null) {
            dto.setElectionId(discussion.getElection().getId());
            dto.setElectionTitle(discussion.getElection().getTitle());
        }

        List<DiscussionComment> comments = commentRepository.findByDiscussionIdOrderByCreatedAtAsc(discussion.getId());
        dto.setComments(comments.stream().map(comment -> {
            DiscussionDTO.CommentDTO cDto = modelMapper.map(comment, DiscussionDTO.CommentDTO.class);
            cDto.setAuthorName(comment.getAuthor().getFullName());
            cDto.setAuthorId(comment.getAuthor().getId());
            return cDto;
        }).collect(Collectors.toList()));

        return dto;
    }
}
