package ru.mtuci.demo.model.request;

import lombok.*;

@Data
@Getter
@Builder
@AllArgsConstructor
public class SignaturesUpdatedAfterRequest {

    private String since;

}
