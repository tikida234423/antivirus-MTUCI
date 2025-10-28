package ru.mtuci.demo.model.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class AllLicensesRequest {

    private String name;

    private String macAddress;

}
