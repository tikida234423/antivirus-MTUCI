package ru.mtuci.demo.controller;

import com.fasterxml.jackson.core.util.RecyclerPool;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.demo.configuration.JwtTokenProvider;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.model.Device;
import ru.mtuci.demo.model.Ticket;
import ru.mtuci.demo.model.request.*;
import ru.mtuci.demo.model.response.*;
import ru.mtuci.demo.service.DeviceService;
import ru.mtuci.demo.service.LicenseService;
import ru.mtuci.demo.service.LicenseTypeService;
import ru.mtuci.demo.service.ProductService;
import ru.mtuci.demo.service.impl.UserDetailsServiceImpl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/license")
@RequiredArgsConstructor
public class LicenseController {

    private final ProductService productService;
    private final UserDetailsServiceImpl userDetailsService;
    private final LicenseTypeService licenseTypeService;
    private final JwtTokenProvider jwtTokenProvider;
    private final LicenseService licenseService;
    private final DeviceService deviceService;


    @PostMapping("/info")
    public ResponseEntity<?> getLicenseInfo(@RequestBody LicenseInfoRequest request,
                                  @RequestHeader("Authorization") String authHeader) {

        LicenseInfoResponse response = new LicenseInfoResponse();

        try {

            authHeader = authHeader.replace("Bearer ", "");

            String email = jwtTokenProvider.getUsername(authHeader);
            ApplicationUser user = userDetailsService.getUserByEmail(email);
            Optional<Device> device = deviceService.getDeviceByInfo(user,
                                                                    request.getMacAddress(),
                                                                    request.getName());

            if (device.isEmpty()) {

                response.setStatus("The device not found");

                return ResponseEntity.badRequest()
                        .body(response);

            }

            Ticket ticket = licenseService.getActiveLicensesForDevice(device.get(),
                                                                        request.getActivationCode());

            if (!ticket.getStatus().equals("Ok")) {

                response.setStatus(ticket.getInfo());

                return ResponseEntity.badRequest()
                        .body(response);

            }

            response.setTicket(ticket);
            response.setStatus("Ok");

            return ResponseEntity.ok(response);
        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return ResponseEntity.internalServerError()
                    .body(response);

        }

    }

    @PostMapping("/all")
    public ResponseEntity<?> getAllLicenses(@RequestBody AllLicensesRequest request,
                                 @RequestHeader("Authorization") String authHeader) {

        AllLicensesResponse response = new AllLicensesResponse();

        try {

            authHeader = authHeader.replace("Bearer ", "");

            String email = jwtTokenProvider.getUsername(authHeader);

            ApplicationUser user = userDetailsService.getUserByEmail(email);

            Optional<Device> device = deviceService.getDeviceByInfo(user,
                                                                    request.getMacAddress(),
                                                                    request.getName());

            if (device.isEmpty()) {

                response.setStatus("The device not found");

                return ResponseEntity.badRequest()
                        .body(response);

            }

            List<String> licensesForDevice = licenseService.getAllLicensesForDevice(device.get());

            response.setLicenses(licensesForDevice);
            response.setStatus("Ok");

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return ResponseEntity.internalServerError()
                    .body(response);

        }

    }

    @PostMapping("/renewal")
    public ResponseEntity<?> renewalLicense(@RequestBody LicenseRenewalRequest request,
                                            @RequestHeader("Authorization") String authHeader) {

        LicenseRenewalResponse response = new LicenseRenewalResponse();

        try {

            authHeader = authHeader.replace("Bearer ", "");

            String email = jwtTokenProvider.getUsername(authHeader);
            ApplicationUser user = userDetailsService.getUserByEmail(email);

            Ticket ticket = licenseService.renewalLicense(request.getActivationCode(), user);

            if (!ticket.getStatus().equals("Ok")) {

                response.setStatus(ticket.getInfo());

                return ResponseEntity.badRequest()
                        .body(response);

            }

            response.setTicket(ticket);
            response.setStatus("Ok");

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return ResponseEntity.internalServerError()
                    .body(response);

        }

    }

    @GetMapping("/renewal/all")
    public ResponseEntity<?> getAllRenewal(@RequestHeader("Authorization") String authHeader) {

        AllRenewalResponse response = new AllRenewalResponse();

        try {

            authHeader = authHeader.replace("Bearer ", "");

            String email = jwtTokenProvider.getUsername(authHeader);
            ApplicationUser user = userDetailsService.getUserByEmail(email);

            List<String> allRenewalList = licenseService.getAllLicensesRenewalForUser(user);

            response.setRenewalList(allRenewalList);
            response.setStatus("Ok");

            return ResponseEntity.ok(allRenewalList);

        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return ResponseEntity.internalServerError()
                    .body(response);

        }

    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<?> createLicense(@RequestBody LicenseCreateRequest request,
                                           @RequestHeader("Authorization") String authHeader) {

        LicenseCreateResponse response = new LicenseCreateResponse();

        try {

            Long productId = request.getProductId();
            Long ownerId = request.getOwnerId();
            Long licenseTypeId = request.getLicenseTypeId();

            if (productService.getProductById(productId) == null) {

                response.setStatus("Product does not exist");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(response);

            }

            if (productService.getProductById(productId).isBlocked()) {

                response.setStatus("Product is blocked");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(response);

            }

            if (userDetailsService.getUserById(ownerId) == null) {

                response.setStatus("Owner does not exist");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(response);

            }

            if (licenseTypeService.getLicenseTypeById(licenseTypeId) == null) {

                response.setStatus("License type does not exist");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(response);

            }

            authHeader = authHeader.replace("Bearer ", "");

            String email = jwtTokenProvider.getUsername(authHeader);
            ApplicationUser user = userDetailsService.getUserByEmail(email);

            Long id = licenseService.createLicense(productId,
                                                    ownerId,
                                                    licenseTypeId,
                                                    user,
                                                    request.getCount());

            response.setId(id);
            response.setStatus("Ok");

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return ResponseEntity.internalServerError()
                    .body(response);

        }

    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateLicense(@RequestBody LicenseActivateRequest request,
                                             @RequestHeader("Authorization") String authHeader) {

        LicenseActivateResponse response = new LicenseActivateResponse();

        try {

            authHeader = authHeader.replace("Bearer ", "");

            String email = jwtTokenProvider.getUsername(authHeader);
            ApplicationUser user = userDetailsService.getUserByEmail(email);
            Device device = deviceService.registerOrUpdateDevice(request.getMacAddress(),
                                                                request.getName(),
                                                                user);

            Ticket ticket = licenseService.activateLicense(request.getActivationCode(),
                                                        device, user);

            if (!ticket.getStatus().equals("Ok")) {

                response.setStatus(ticket.getInfo());

                return ResponseEntity.badRequest()
                        .body(response);

            }

            response.setTicket(ticket);
            response.setStatus("Ok");

            return ResponseEntity.ok(ticket);

        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return ResponseEntity.internalServerError()
                    .body(response);

        }

    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<?> updateLicense(@RequestBody LicenseUpdateRequest request) {

        LicenseUpdateResponse response = new LicenseUpdateResponse();

        try {

            String result = licenseService.updateLicense(request.getId(),
                    request.getOwnerId(),
                    request.getProductId(),
                    request.getTypeId(),
                    request.getIsBlocked(),
                    request.getDescription(),
                    request.getDeviceCount()
                    );

            if (!Objects.equals(result, "Ok")) {

                response.setStatus(result);

                return ResponseEntity.badRequest()
                        .body(response);

            }

            response.setStatus("Ok");

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return ResponseEntity.internalServerError()
                    .body(response);

        }

    }

}
