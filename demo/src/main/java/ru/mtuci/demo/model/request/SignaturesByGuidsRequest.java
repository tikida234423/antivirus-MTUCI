package ru.mtuci.demo.model.request;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Getter
@Builder
@AllArgsConstructor
public class SignaturesByGuidsRequest {

    private List<UUID> guids;

}
