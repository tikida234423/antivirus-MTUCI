package ru.mtuci.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.demo.model.Device;
import ru.mtuci.demo.model.LicenseType;
import ru.mtuci.demo.model.request.LicenseTypeCreateRequest;
import ru.mtuci.demo.model.request.LicenseTypeUpdateRequest;
import ru.mtuci.demo.model.response.GetAllDevicesResponse;
import ru.mtuci.demo.model.response.GetAllLicenseTypesResponse;
import ru.mtuci.demo.model.response.LicenseTypeCreateResponse;
import ru.mtuci.demo.model.response.LicenseTypeUpdateResponse;
import ru.mtuci.demo.service.LicenseTypeService;

import java.util.ArrayList;

@Controller
@RequestMapping("/license/type")
@RequiredArgsConstructor
public class LicenseTypeController {

    private final LicenseTypeService licenseTypeService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<?> createLicenseType(@RequestBody LicenseTypeCreateRequest request) {

        LicenseTypeCreateResponse response = new LicenseTypeCreateResponse();

        try {

            Long id = licenseTypeService.createLicenseType(request.getDuration(),
                                                            request.getDescription(),
                                                            request.getName());

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

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<?> updateLicenseType(@RequestBody LicenseTypeUpdateRequest request) {

        LicenseTypeUpdateResponse response = new LicenseTypeUpdateResponse();

        try {

            Boolean result = licenseTypeService.updateLicenseType(request.getId(),
                                                                request.getDuration(),
                                                                request.getDescription(),
                                                                request.getName());

            if (!result) {

                response.setStatus("License not found");

                return ResponseEntity.badRequest()
                        .body(response);

            }

            response.setStatus("Ok");

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return  ResponseEntity.internalServerError()
                    .body(response);

        }

    }

}
