package ru.mtuci.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.model.LicenseHistory;

import java.util.Optional;

public interface LicenseHistoryRepository extends JpaRepository<LicenseHistory, Long> {

    Optional<LicenseHistory> findById(Long id);

}
