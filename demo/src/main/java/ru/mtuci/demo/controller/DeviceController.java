package ru.mtuci.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.demo.configuration.JwtTokenProvider;
import ru.mtuci.demo.model.Device;
import ru.mtuci.demo.model.request.DeviceUpdateRequest;
import ru.mtuci.demo.model.response.DeviceCreateResponse;
import ru.mtuci.demo.model.response.DeviceDeleteResponse;
import ru.mtuci.demo.model.response.DeviceUpdateResponse;
import ru.mtuci.demo.model.response.GetAllDevicesResponse;
import ru.mtuci.demo.service.DeviceService;
import ru.mtuci.demo.service.impl.UserDetailsServiceImpl;

import java.util.ArrayList;
import java.util.UUID;

@Controller
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<?> getAllDevices(@RequestParam(value = "id", required = false) Long id) {

        GetAllDevicesResponse response = new GetAllDevicesResponse();

        try {

            if (id != null) {

                Device device = deviceService.getDeviceById(id);

                if (device == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Device not found");
                }

                response.setDevices(new ArrayList<>());
                response.getDevices().add(device);
                response.setStatus("Ok");

                return ResponseEntity.ok(response);
            }

            response.setDevices(deviceService.getAllDevices());
            response.setStatus("Ok");

            return ResponseEntity.ok(response);
        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<?> createDevice(@RequestBody Device device,
                                          @RequestHeader("Authorization") String authHeader) {
        DeviceCreateResponse response = new DeviceCreateResponse();

        authHeader = authHeader.replace("Bearer ", "");

        try {

            if (deviceService.getDeviceByMacAddress(device.getMacAddress()) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Device with this mac address already exists");
            }

            if (device.getUser() == null) {
                device.setUser(userDetailsServiceImpl.getUserByEmail(jwtTokenProvider.getUsername(authHeader)));
            }

            deviceService.saveDevice(device);

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
    public ResponseEntity<?> updateDevice(@RequestBody DeviceUpdateRequest request) {

        DeviceUpdateResponse response = new DeviceUpdateResponse();

        try {

            if (deviceService.getDeviceById(request.getId()) == null) {

                response.setStatus("Device not found");

                return ResponseEntity.badRequest()
                        .body(response);

            }

            Device device = new Device();
            device.setId(request.getId());
            device.setMacAddress(request.getMacAddress());
            device.setName(request.getName());
            device.setUser(userDetailsServiceImpl.getUserById(request.getUserId()));

            deviceService.saveDevice(device);

            response.setStatus("Ok");

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return ResponseEntity.internalServerError()
                    .body(response);

        }

    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<?> deleteDevice(@RequestParam("id") Long id) {

        DeviceDeleteResponse response = new DeviceDeleteResponse();

        try {

            Device device = deviceService.getDeviceById(id);

            if (device == null) {

                response.setStatus("Device not found");

                return ResponseEntity.badRequest()
                        .body(response);

            }

            deviceService.deleteDevice(device.getId());

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
