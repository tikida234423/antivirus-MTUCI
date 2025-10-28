package ru.mtuci.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "device")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Device {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String macAddress;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "userId")
    private ApplicationUser user;
}
