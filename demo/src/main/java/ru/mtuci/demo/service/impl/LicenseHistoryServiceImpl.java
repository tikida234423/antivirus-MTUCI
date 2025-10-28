package ru.mtuci.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.model.License;
import ru.mtuci.demo.model.LicenseHistory;
import ru.mtuci.demo.repository.LicenseHistoryRepository;
import ru.mtuci.demo.service.LicenseHistoryService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class LicenseHistoryServiceImpl implements LicenseHistoryService {

    private final LicenseHistoryRepository licenseHistoryRepository;

    public LicenseHistory createNewRecord(String status,
                                          String description,
                                          ApplicationUser user,
                                          License license) {

        LicenseHistory newHistory = new LicenseHistory();
        newHistory.setLicense(license);
        newHistory.setStatus(status);
        newHistory.setChangeDate(new Date());
        newHistory.setDescription(description);
        newHistory.setUser(user);

        return licenseHistoryRepository.save(newHistory);

    }

}
