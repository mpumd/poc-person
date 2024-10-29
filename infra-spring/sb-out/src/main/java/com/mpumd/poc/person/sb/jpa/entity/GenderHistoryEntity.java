package com.mpumd.poc.person.sb.jpa.entity;

import com.mpumd.poc.person.context.aggregat.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "GENDER")
@Entity
public class GenderHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private PersonEntity person;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "changeDate")
    private LocalDateTime changeDate;

    /** * just for the query search */
    public GenderHistoryEntity(Gender gender, LocalDateTime changeDate) {
        this.gender = gender;
        this.changeDate = changeDate;
    }
}
