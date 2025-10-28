package ru.mtuci.demo.model.request;

import lombok.*;

@Data
@Getter
@Builder
@AllArgsConstructor
public class SignaturesAddRequest {

    private String threatName;

    private String firstBytes;

    private String hash;

    private Integer remainderLength;

    private String fileType;

    private Integer offsetStart;

    private Integer offsetEnd;

}
