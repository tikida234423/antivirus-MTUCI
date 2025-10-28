package ru.mtuci.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.model.DeviceLicense;

import java.util.List;
import java.util.Optional;

public interface DeviceLicenseRepository extends JpaRepository<DeviceLicense, Long> {

    Optional<DeviceLicense> findById(Long id);
    List<DeviceLicense> findByDeviceId(Long deviceId);
    Long countByLicenseId(Long licenseId);

}
