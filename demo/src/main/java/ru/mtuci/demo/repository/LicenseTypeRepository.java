package ru.mtuci.demo.repository;

import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.model.LicenseType;

import java.util.Optional;

public interface LicenseTypeRepository extends JpaRepository<LicenseType, Long> {

    Optional<LicenseType> findById(Long id);
    Optional<LicenseType> findTopByOrderByIdDesc();

}
