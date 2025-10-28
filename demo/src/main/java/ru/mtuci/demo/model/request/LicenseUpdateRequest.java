package ru.mtuci.demo.model.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class LicenseUpdateRequest {

    private Long id;

    private Long ownerId;

    private Long productId;

    private Long typeId;

    private Boolean isBlocked;

    private String description;

    private Long deviceCount;

}
