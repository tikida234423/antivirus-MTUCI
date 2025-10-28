package ru.mtuci.demo.model.request;

import lombok.*;

import java.util.UUID;

@Data
@Getter
@Builder
@AllArgsConstructor
public class SignaturesDeleteRequest {

    private UUID signatureUUID;

}
