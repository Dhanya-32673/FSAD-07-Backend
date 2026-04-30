package com.electionmonitor.config;

import com.electionmonitor.model.*;
import com.electionmonitor.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final PollingStationRepository pollingStationRepository;
    private final IncidentRepository incidentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedElectionsAndCandidates();
        seedIncidents();
    }

    
    private void seedUsers() {
        if (userRepository.count() > 0) return;

        userRepository.save(User.builder()
                .username("admin").email("2400032673cse1@gmail.com")
                .password(passwordEncoder.encode("admin123"))
                .fullName("System Administrator").role(User.Role.ADMIN)
                .phone("1234567890").enabled(true)
                .approvalStatus(User.ApprovalStatus.APPROVED).build());

        userRepository.save(User.builder()
                .username("citizen1").email("citizen@election.com")
                .password(passwordEncoder.encode("citizen123"))
                .fullName("John Citizen").role(User.Role.CITIZEN)
                .phone("9876543210").enabled(true)
                .approvalStatus(User.ApprovalStatus.APPROVED).build());

        userRepository.save(User.builder()
                .username("observer1").email("observer@election.com")
                .password(passwordEncoder.encode("observer123"))
                .fullName("Jane Observer").role(User.Role.OBSERVER)
                .phone("5551234567").enabled(true)
                .approvalStatus(User.ApprovalStatus.APPROVED).build());

        userRepository.save(User.builder()
                .username("analyst1").email("analyst@election.com")
                .password(passwordEncoder.encode("analyst123"))
                .fullName("Data Analyst Smith").role(User.Role.ANALYST)
                .phone("5559876543").enabled(true)
                .approvalStatus(User.ApprovalStatus.APPROVED).build());

        System.out.println("✅ Users seeded.");
    }

    
    private void seedElectionsAndCandidates() {
        if (electionRepository.count() > 0) return;

        User admin = userRepository.findByUsername("admin").orElse(userRepository.findAll().get(0));

        Election e1 = electionRepository.save(Election.builder()
                .title("2024 National Presidential Election")
                .description("The national presidential election to elect the next president of the country.")
                .electionType(Election.ElectionType.NATIONAL)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(5))
                .status(Election.ElectionStatus.ACTIVE)
                .createdBy(admin).build());

        Election e2 = electionRepository.save(Election.builder()
                .title("State Assembly Elections 2024")
                .description("State-level assembly elections for representative seats.")
                .electionType(Election.ElectionType.STATE)
                .startDate(LocalDateTime.now().plusDays(30))
                .endDate(LocalDateTime.now().plusDays(31))
                .status(Election.ElectionStatus.UPCOMING)
                .createdBy(admin).build());

        Election e3 = electionRepository.save(Election.builder()
                .title("City Municipal Corporation Election")
                .description("Local municipal corporation elections for city governance.")
                .electionType(Election.ElectionType.LOCAL)
                .startDate(LocalDateTime.now().minusDays(30))
                .endDate(LocalDateTime.now().minusDays(29))
                .status(Election.ElectionStatus.COMPLETED)
                .createdBy(admin).build());

        
        candidateRepository.save(Candidate.builder().name("Alice Johnson").party("Progressive Party")
                .manifesto("Education reform, healthcare for all, climate action").election(e1).build());
        candidateRepository.save(Candidate.builder().name("Bob Williams").party("Conservative Union")
                .manifesto("Economic growth, tax reform, national security").election(e1).build());
        candidateRepository.save(Candidate.builder().name("Carol Davis").party("Independent Alliance")
                .manifesto("Transparency, anti-corruption, digital governance").election(e1).build());
        candidateRepository.save(Candidate.builder().name("David Lee").party("People's Front")
                .manifesto("Rural development, farmer welfare").election(e2).build());
        candidateRepository.save(Candidate.builder().name("Eva Martinez").party("Urban Development Party")
                .manifesto("Infrastructure, urban planning, smart cities").election(e2).build());
        candidateRepository.save(Candidate.builder().name("Frank Brown").party("City First")
                .manifesto("Better roads, sanitation, public transport").election(e3).build());
        candidateRepository.save(Candidate.builder().name("Grace Wilson").party("Green City Alliance")
                .manifesto("Parks, clean energy, waste management").election(e3).build());

        
        pollingStationRepository.save(PollingStation.builder()
                .name("Lincoln Community Center").location("123 Main St, District A")
                .region("North").totalVoters(5000).votesCast(3200)
                .status(PollingStation.StationStatus.IN_PROGRESS).election(e1).build());
        pollingStationRepository.save(PollingStation.builder()
                .name("Washington High School").location("456 Oak Ave, District B")
                .region("South").totalVoters(4500).votesCast(2800)
                .status(PollingStation.StationStatus.IN_PROGRESS).election(e1).build());
        pollingStationRepository.save(PollingStation.builder()
                .name("Jefferson Library").location("789 Elm Dr, District C")
                .region("East").totalVoters(3800).votesCast(1500)
                .status(PollingStation.StationStatus.IN_PROGRESS).election(e1).build());
        pollingStationRepository.save(PollingStation.builder()
                .name("Roosevelt Park Hall").location("321 Pine Rd, District D")
                .region("West").totalVoters(6000).votesCast(0)
                .status(PollingStation.StationStatus.NOT_STARTED).election(e1).build());

        System.out.println("✅ Elections, candidates & polling stations seeded.");
    }

    
    private void seedIncidents() {
        if (incidentRepository.count() > 0) return;

        Election election = electionRepository.findAll().stream().findFirst().orElse(null);
        User reporter = userRepository.findByUsername("citizen1")
                .orElse(userRepository.findAll().stream().findFirst().orElse(null));
        if (election == null || reporter == null) return;

        incidentRepository.save(Incident.builder()
                .title("Unauthorized voter list alteration attempt")
                .description("Suspected unauthorized access to voter registry database at booth 7-B.")
                .category(Incident.IncidentCategory.FRAUD)
                .severity(Incident.Severity.HIGH)
                .status(Incident.IncidentStatus.REPORTED)
                .location("Booth 7-B, District A")
                .election(election).reportedBy(reporter)
                .reportedAt(LocalDateTime.now().minusHours(3)).build());

        incidentRepository.save(Incident.builder()
                .title("Polling station equipment malfunction")
                .description("EVM machine at Lincoln Community Center reported erratic behavior during testing.")
                .category(Incident.IncidentCategory.TECHNICAL)
                .severity(Incident.Severity.MEDIUM)
                .status(Incident.IncidentStatus.UNDER_REVIEW)
                .location("Lincoln Community Center, North")
                .election(election).reportedBy(reporter)
                .reportedAt(LocalDateTime.now().minusHours(6)).build());

        incidentRepository.save(Incident.builder()
                .title("Suspicious campaigning near polling zone")
                .description("Campaign materials distributed within prohibited 200m zone of polling station.")
                .category(Incident.IncidentCategory.IRREGULARITY)
                .severity(Incident.Severity.LOW)
                .status(Incident.IncidentStatus.RESOLVED)
                .location("Washington High School, District B")
                .election(election).reportedBy(reporter)
                .reportedAt(LocalDateTime.now().minusDays(1)).build());

        incidentRepository.save(Incident.builder()
                .title("CRITICAL: Coordinated voter suppression attempt")
                .description("Multiple reports of voters being intimidated outside polling stations in District D.")
                .category(Incident.IncidentCategory.VIOLENCE)
                .severity(Incident.Severity.CRITICAL)
                .status(Incident.IncidentStatus.REPORTED)
                .location("Roosevelt Park Hall, District D")
                .election(election).reportedBy(reporter)
                .reportedAt(LocalDateTime.now().minusMinutes(45)).build());

        incidentRepository.save(Incident.builder()
                .title("Discrepancy in vote count at closing")
                .description("Minor discrepancy detected in paper trail vs EVM count at Jefferson Library.")
                .category(Incident.IncidentCategory.OTHER)
                .severity(Incident.Severity.MEDIUM)
                .status(Incident.IncidentStatus.DISMISSED)
                .location("Jefferson Library, District C")
                .election(election).reportedBy(reporter)
                .reportedAt(LocalDateTime.now().minusHours(10)).build());

        System.out.println("✅ Sample incidents seeded.");
    }
}
