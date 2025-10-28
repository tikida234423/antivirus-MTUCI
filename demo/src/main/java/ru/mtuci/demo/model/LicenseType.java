package ru.mtuci.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "license_types")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class LicenseType {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Long defaultDuration;

    private String description;

}
