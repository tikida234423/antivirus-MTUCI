package ru.mtuci.demo.service;

import ru.mtuci.demo.model.LicenseType;

public interface LicenseTypeService {

    public LicenseType getLicenseTypeById(Long id);

    public Long createLicenseType(Long duration,
                                  String description,
                                  String name);

    public Boolean updateLicenseType(Long id,
                                     Long newDuration,
                                     String newDescription,
                                     String newName);

}
