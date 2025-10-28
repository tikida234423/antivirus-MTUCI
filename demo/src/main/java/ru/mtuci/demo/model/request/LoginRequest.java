package ru.mtuci.demo.model.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class LoginRequest {

    private String email;
    private String password;
    private Long deviceId;

}
