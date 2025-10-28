package ru.mtuci.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.*;
import ru.mtuci.demo.repository.LicenseRepository;
import ru.mtuci.demo.service.*;

import java.security.PrivateKey;
import java.security.Signature;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LicenseServiceImpl implements LicenseService {

    private final LicenseTypeService licenseTypeService;
    private final ProductService productService;
    private final LicenseRepository licenseRepository;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final LicenseHistoryService licenseHistoryService;
    private final DeviceLicenseService deviceLicenseService;
    private final PrivateKey privateKey;

    public License getLicenseById(Long id) {
        return licenseRepository.findById(id).orElse(null);
    }

    public License getLicenseByProductId(Long productId) {
        return licenseRepository.findByProductId(productId);
    }

    public Long createLicense(Long productId,
                              Long ownerId,
                              Long licenseTypeId,
                              ApplicationUser applicationUser,
                              Long count) {

        LicenseType licenseType = licenseTypeService.getLicenseTypeById(licenseTypeId);
        Product product = productService.getProductById(productId);
        License license = new License();

        String uid = String.valueOf(UUID.randomUUID());
        while (licenseRepository.findByCode(uid).isPresent()) {
            uid = String.valueOf(UUID.randomUUID());
        }

        license.setCode(uid);
        license.setProduct(product);
        license.setLicenseType(licenseType);
        license.setBlocked(product.isBlocked());
        license.setDeviceCount(count);
        license.setOwnerId(userDetailsServiceImpl.getUserById(ownerId));
        license.setDuration(licenseType.getDefaultDuration());
        license.setDescription(licenseType.getDescription());

        licenseRepository.save(license);

        licenseHistoryService.createNewRecord("Not activated",
                "License created", applicationUser,
                licenseRepository.findTopByOrderByIdDesc().get());

        return licenseRepository.findTopByOrderByIdDesc().get().getId();
    }

    public String makeSignature(Ticket ticket) {

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            String res = objectMapper.writeValueAsString(ticket);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(res.getBytes());

            return Base64.getEncoder().encodeToString(signature.sign());

        }
        catch (Exception e) {

            return "The signature is not valid";

        }

    }

    public Ticket createTicket(ApplicationUser user,
                               Device device,
                               License license,
                               String info,
                               String status) {

        Ticket ticket = new Ticket();
        ticket.setCurrentDate(new Date());

        if (user != null) {

            ticket.setUserId(user.getId());

        }

        if (device != null) {

            ticket.setDeviceId(device.getId());

        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 1);
        ticket.setLifetime(calendar.getTime());

        if (license != null) {

            ticket.setActivationDate(license.getFirstActivationDate());
            ticket.setExpirationDate(license.getEndingDate());
            ticket.setLicenseBlocked(license.isBlocked());

        }

        ticket.setInfo(info);
        ticket.setStatus(status);
        ticket.setDigitalSignature(makeSignature(ticket));

        return ticket;

    }

    public Ticket getActiveLicensesForDevice(Device device,
                                            String code) {

        List<DeviceLicense> deviceLicenseList = deviceLicenseService.getAllLicensesById(device);

        List<Long> licenseIds = deviceLicenseList.stream()
                .map(DeviceLicense::getId).toList();

        Optional<License> license = licenseRepository.findByIdInAndCode(licenseIds, code);

        Ticket ticket = new Ticket();

        if (license.isEmpty()) {

            ticket.setInfo("License was not found");
            ticket.setStatus("Error");

            return ticket;

        }

        ticket = createTicket(license.get().getUser(),
                                device,
                                license.get(),
                                "Info",
                                "Ok");

        return ticket;

    }

    public List<String> getAllLicensesForDevice(Device device) {

        List<DeviceLicense> deviceLicenseList = deviceLicenseService.getAllLicensesById(device);

        return deviceLicenseList.stream()
                .map(license -> license != null ? license.getLicense().getCode() : null)
                .toList();

    }

    public List<String> getAllLicensesRenewalForUser(ApplicationUser user) {

        List<License> licenseList = licenseRepository.findByOwnerId(user);

        return licenseList.stream()
                .map(license -> license != null ? license.getCode() : null)
                .toList();

    }

    public Ticket activateLicense(String code,
                                  Device device,
                                  ApplicationUser user) {

        Ticket ticket = new Ticket();
        Optional<License> license = licenseRepository.findByCode(code);

        if (license.isEmpty()) {

            ticket.setInfo("License was not found");
            ticket.setStatus("Error");

            return ticket;

        }

        License newLicense = license.get();

        if (newLicense.isBlocked()
                || (newLicense.getEndingDate() != null && new Date().after(newLicense.getEndingDate()))
                || (newLicense.getUser() != null && !Objects.equals(newLicense.getUser().getId(), user.getId()))
                || deviceLicenseService.getDeviceCountForLicense(newLicense.getId()) >= newLicense.getDeviceCount()) {

            ticket.setStatus("Activation failed");
            ticket.setStatus("Error");

            return ticket;

        }

        if (newLicense.getFirstActivationDate() == null) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            newLicense.setFirstActivationDate(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, Math.toIntExact(newLicense.getDuration()));

            newLicense.setEndingDate(calendar.getTime());
            newLicense.setUser(user);

        }

        deviceLicenseService.createDeviceLicense(newLicense, device);
        licenseRepository.save(newLicense);
        licenseHistoryService.createNewRecord("Activated",
                                            "Valid license",
                                                user,
                                                newLicense);

        ticket = createTicket(user,
                                device,
                                newLicense,
                                "The license has been activated",
                            "Ok");

        return ticket;

    }

    public String updateLicense(Long id,
                                Long ownerId,
                                Long productId,
                                Long typeId,
                                Boolean isBlocked,
                                String description,
                                Long deviceCount) {

        Optional<License> license = licenseRepository.findById(id);
        if (license.isEmpty()) {

            return "License was not found";

        }

        License newLicense = license.get();
        newLicense.setCode(String.valueOf(UUID.randomUUID()));
        if (productService.getProductById(productId) == null) {

            return "Product was not found";

        }

        newLicense.setProduct(productService.getProductById(productId));
        if (licenseTypeService.getLicenseTypeById(typeId) == null) {

            return "License type was not found";

        }

        newLicense.setLicenseType(licenseTypeService.getLicenseTypeById(typeId));
        newLicense.setBlocked(isBlocked);
        newLicense.setDescription(description);
        newLicense.setDeviceCount(deviceCount);
        newLicense.setDuration(licenseTypeService.getLicenseTypeById(typeId).getDefaultDuration());
        newLicense.setOwnerId(userDetailsServiceImpl.getUserById(ownerId));

        licenseRepository.save(newLicense);

        return "Ok";

    }

    public Ticket renewalLicense(String code,
                                 ApplicationUser user) {

        Ticket ticket = new Ticket();
        Optional<License> license = licenseRepository.findByCode(code);

        if (license.isEmpty()) {

            ticket.setInfo("License was not found");
            ticket.setStatus("Error");

            return ticket;

        }

        License newLicense = license.get();
        if (newLicense.isBlocked()
                || newLicense.getEndingDate() != null && new Date().after(newLicense.getEndingDate())
                || !Objects.equals(newLicense.getOwnerId().getId(), user.getId())
                || newLicense.getFirstActivationDate() == null) {

            ticket.setInfo("Activation failed");
            ticket.setStatus("Error");

            return ticket;

        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newLicense.getEndingDate());
        calendar.add(Calendar.DAY_OF_MONTH, Math.toIntExact(newLicense.getDuration()));

        newLicense.setEndingDate(calendar.getTime());

        licenseRepository.save(newLicense);

        licenseHistoryService.createNewRecord("Renewal",
                                        "Valid license",
                                                user,
                                                newLicense);

        ticket = createTicket(user,
                        null,
                            newLicense,
                            "The license has been renewed",
                            "Ok");

        return ticket;

    }

}
