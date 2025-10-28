package ru.mtuci.demo.model.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class LicenseInfoRequest {

    private String name;
    private String macAddress;
    private String activationCode;

}
