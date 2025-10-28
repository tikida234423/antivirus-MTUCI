package ru.mtuci.demo.model.request;

import lombok.*;

@Data
@Getter
@Builder
@AllArgsConstructor
public class ProductUpdateRequest {

    private Long productId;

    private String name;

    private Boolean isBlocked;

}
