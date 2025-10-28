package ru.mtuci.demo.model.request;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import ru.mtuci.demo.model.Role;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserUpdateRequest {

    private Long id;

    private String email;

    private String username;

    private String password;

    private Role role;

}
