package ru.mtuci.demo.model.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDeleteRequest {

    String email;

}
