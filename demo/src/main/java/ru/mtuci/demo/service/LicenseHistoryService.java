package ru.mtuci.demo.service;


import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.model.License;
import ru.mtuci.demo.model.LicenseHistory;

public interface LicenseHistoryService {

    public LicenseHistory createNewRecord(String status,
                                          String description,
                                          ApplicationUser user,
                                          License license);

}
