package ru.mtuci.demo.service;

import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.model.Device;

import java.util.List;
import java.util.Optional;

public interface DeviceService {

    Optional<Device> getDeviceByInfo(ApplicationUser user, String mac_address, String name);

    void deleteLastDevice(ApplicationUser user);

    Device registerOrUpdateDevice(String mac, String name, ApplicationUser user);

    Device getDeviceByMacAddress(String macAddress);

    Device saveDevice(Device device);

    Device getDeviceById(Long id);

    List<Device> getAllDevices();

    void deleteDevice(Long id);

}
