package ru.mtuci.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.LicenseType;
import ru.mtuci.demo.repository.LicenseTypeRepository;
import ru.mtuci.demo.service.LicenseTypeService;

@Service
@RequiredArgsConstructor
public class LicenseTypeServiceImpl implements LicenseTypeService {

    private final LicenseTypeRepository licenseTypeRepository;

    public LicenseType getLicenseTypeById(Long id) {

        return licenseTypeRepository.findById(id).orElse(null);

    }

    public Long createLicenseType(Long duration,
                                  String description,
                                  String name) {

        LicenseType licenseType = new LicenseType();
        licenseType.setDescription(description);
        licenseType.setName(name);
        licenseType.setDefaultDuration(duration);
        licenseTypeRepository.save(licenseType);

        return licenseTypeRepository.findTopByOrderByIdDesc().get().getId();

    }

    public Boolean updateLicenseType(Long id,
                                    Long newDuration,
                                    String newDescription,
                                    String newName) {

        LicenseType licenseType = getLicenseTypeById(id);

        if (licenseType == null) {
            return false;
        }

        LicenseType newLicenseType = new LicenseType();
        newLicenseType.setDefaultDuration(newDuration);
        newLicenseType.setDescription(newDescription);
        newLicenseType.setName(newName);

        licenseTypeRepository.save(newLicenseType);

        return true;

    }

}
