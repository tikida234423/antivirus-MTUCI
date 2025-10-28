package ru.mtuci.demo.model.request;

import lombok.*;

@Data
@Getter
@Builder
@AllArgsConstructor
public class LicenseActivateRequest {

    private String activationCode;

    private String name;

    private String macAddress;

}
