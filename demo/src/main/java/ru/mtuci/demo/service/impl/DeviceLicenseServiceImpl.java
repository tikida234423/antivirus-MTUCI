package ru.mtuci.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.Device;
import ru.mtuci.demo.model.DeviceLicense;
import ru.mtuci.demo.model.License;
import ru.mtuci.demo.repository.DeviceLicenseRepository;
import ru.mtuci.demo.service.DeviceLicenseService;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceLicenseServiceImpl implements DeviceLicenseService {

    private final DeviceLicenseRepository deviceLicenseRepository;

    public Long getDeviceCountForLicense(Long licenseId) {

        return deviceLicenseRepository.countByLicenseId(licenseId);

    }

    public List<DeviceLicense> getAllLicensesById(Device device) {
        return deviceLicenseRepository.findByDeviceId(device.getId());
    }

    public DeviceLicense createDeviceLicense(License license,
                                             Device device) {

        DeviceLicense newLicense = new DeviceLicense();
        newLicense.setLicense(license);
        newLicense.setDevice(device);
        newLicense.setActivationDate(new Date());
        return deviceLicenseRepository.save(newLicense);

    }

    public void deleteById(Long id) {
        deviceLicenseRepository.deleteById(id);
    }

}
