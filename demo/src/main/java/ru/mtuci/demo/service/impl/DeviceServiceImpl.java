package ru.mtuci.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.model.Device;
import ru.mtuci.demo.model.DeviceLicense;
import ru.mtuci.demo.repository.DeviceLicenseRepository;
import ru.mtuci.demo.repository.DeviceRepository;
import ru.mtuci.demo.service.DeviceLicenseService;
import ru.mtuci.demo.service.DeviceService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceLicenseService deviceLicenseService;

    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id).orElse(null);
    }

    public Optional<Device> getDeviceByInfo(ApplicationUser user, String mac_address, String name) {
        return deviceRepository.findByUserAndMacAddressAndName(user, mac_address, name);
    }

    @Override
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public void deleteLastDevice(ApplicationUser user) {
        Optional<Device> lastDevice = deviceRepository.findTopByUserOrderByIdDesc(user);
        lastDevice.ifPresent(deviceRepository::delete);
    }

    public Device registerOrUpdateDevice(String mac, String name, ApplicationUser user) {

        Device newDevice = getDeviceByInfo(user, mac, name)
                .orElse(new Device());

        newDevice.setName(name);
        newDevice.setMacAddress(mac);
        newDevice.setUser(user);

        return deviceRepository.save(newDevice);

    }

    public Device saveDevice(Device device) {
        return deviceRepository.save(device);
    }

    @Override
    public Device getDeviceByMacAddress(String macAddress) {
        return deviceRepository.findByMacAddress(macAddress);
    }

    public void deleteDevice(Long id) {

        Device device = deviceRepository.findById(id).orElse(null);

        List<DeviceLicense> applicationDeviceLicenses = deviceLicenseService.getAllLicensesById(device);

        if (!applicationDeviceLicenses.isEmpty()) {
            for (DeviceLicense i : applicationDeviceLicenses) {
                deviceLicenseService.deleteById(i.getId());
            }
        }

        deviceRepository.deleteById(id);

    }

}
