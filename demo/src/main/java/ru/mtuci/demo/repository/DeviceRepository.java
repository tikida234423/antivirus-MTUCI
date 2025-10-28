package ru.mtuci.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.model.Device;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findById(Long id);
    Optional<Device> findByUserAndMacAddressAndName(ApplicationUser user, String mac_address, String name);
    Optional<Device> findByIdAndUser(Long id, ApplicationUser user);
    Optional<Device> findTopByUserOrderByIdDesc(ApplicationUser user);
    Device findByMacAddress(String macAddress);

}
