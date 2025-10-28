package ru.mtuci.demo.service;

import ru.mtuci.demo.model.Device;
import ru.mtuci.demo.model.DeviceLicense;
import ru.mtuci.demo.model.License;

import java.util.List;
import java.util.UUID;

public interface DeviceLicenseService {

    public Long getDeviceCountForLicense(Long licenseId);

    public List<DeviceLicense> getAllLicensesById(Device device);

    public DeviceLicense createDeviceLicense(License license,
                                             Device device);

    void deleteById(Long id);

}
