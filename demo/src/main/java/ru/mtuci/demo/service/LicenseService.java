package ru.mtuci.demo.service;

import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.model.Device;
import ru.mtuci.demo.model.License;
import ru.mtuci.demo.model.Ticket;

import java.util.List;
import java.util.Optional;

public interface LicenseService {

    public License getLicenseById(Long id);

    License getLicenseByProductId(Long productId);

    Long createLicense(Long productId,
                              Long ownerId,
                              Long licenseTypeId,
                              ApplicationUser applicationUser,
                              Long count);

    public String makeSignature(Ticket ticket);

    public Ticket createTicket(ApplicationUser user,
                               Device device,
                               License license,
                               String info,
                               String status);

    public Ticket getActiveLicensesForDevice(Device device,
                                            String code);

    public List<String> getAllLicensesForDevice(Device device);

    public List<String> getAllLicensesRenewalForUser(ApplicationUser user);

    public Ticket activateLicense(String code,
                                  Device device,
                                  ApplicationUser user);

    public String updateLicense(Long id,
                                Long ownerId,
                                Long productId,
                                Long typeId,
                                Boolean isBlocked,
                                String description,
                                Long deviceCount);

    public Ticket renewalLicense(String code,
                                 ApplicationUser user);

}
