package ru.mtuci.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.model.License;

import java.util.List;
import java.util.Optional;

public interface LicenseRepository extends JpaRepository<License, Long> {

    Optional<License> findById(Long id);
    Optional<License> findTopByOrderByIdDesc();
    Optional<License> findByCode(String code);
    Optional<License> findByIdInAndCode(List<Long> ids, String code);
    List<License> findByOwnerId(ApplicationUser ownerId);
    License findByProductId(Long productId);

}
