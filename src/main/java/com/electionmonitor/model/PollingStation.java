package com.electionmonitor.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "polling_stations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollingStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 200)
    private String location;

    @Column(length = 100)
    private String region;

    @Column(name = "total_voters")
    private int totalVoters;

    @Column(name = "votes_cast")
    private int votesCast;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    public enum StationStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }
}
