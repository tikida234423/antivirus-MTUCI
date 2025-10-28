package ru.mtuci.demo.model.request;

import lombok.*;
import ru.mtuci.demo.model.SignatureStatus;

@Data
@Getter
@Builder
@AllArgsConstructor
public class SignaturesByStatusRequest {

    private SignatureStatus status;

}
