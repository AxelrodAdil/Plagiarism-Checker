package kz.kaznu.antiplagiarism.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "result")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    @Id
    @SequenceGenerator(name = "result_seq", sequenceName = "result_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "result_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "res")
    private Double result;
    @Column(name = "date")
    private String date;
}
