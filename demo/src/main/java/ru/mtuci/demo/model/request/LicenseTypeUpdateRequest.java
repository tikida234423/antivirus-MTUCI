package ru.mtuci.demo.model.request;

import lombok.*;

@Data
@Getter
@Builder
@AllArgsConstructor
public class LicenseTypeUpdateRequest {

    private Long id;

    private Long duration;

    private String description;

    private String name;

}
