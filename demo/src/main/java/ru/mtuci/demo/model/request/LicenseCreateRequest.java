package ru.mtuci.demo.model.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class LicenseCreateRequest {

    private Long productId;
    private Long ownerId;
    private Long licenseTypeId;
    private Long count;

}
