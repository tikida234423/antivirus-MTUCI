package ru.mtuci.demo.model.request;

import lombok.*;
import ru.mtuci.demo.model.SignatureStatus;

import java.util.UUID;

@Data
@Getter
@Builder
@AllArgsConstructor
public class SignaturesUpdateRequest {

    private UUID signatureId;

    private String threatName;

    private String firstBytes;

    private String hash;

    private Integer remainderLength;

    private String fileType;

    private Integer offsetStart;

    private Integer offsetEnd;

    private SignatureStatus status;

}