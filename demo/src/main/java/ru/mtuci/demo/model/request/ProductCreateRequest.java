package ru.mtuci.demo.model.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ProductCreateRequest {

    private String name;
    private Boolean isBlocked;

}
