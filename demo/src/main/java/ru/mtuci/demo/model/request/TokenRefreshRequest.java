package ru.mtuci.demo.model.request;

import lombok.*;

@Data
@Getter
@Builder
@AllArgsConstructor
public class TokenRefreshRequest {

    private String refreshToken;

    private Long deviceId;

}
