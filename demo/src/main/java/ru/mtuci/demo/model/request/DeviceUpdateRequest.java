package ru.mtuci.demo.model.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class DeviceUpdateRequest {

    private Long id;
    private String macAddress;
    private String name;
    private Long userId;

}
