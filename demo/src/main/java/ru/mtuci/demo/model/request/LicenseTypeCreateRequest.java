package ru.mtuci.demo.model.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class LicenseTypeCreateRequest {

    private Long duration;

    private String description;

    private String name;

}
