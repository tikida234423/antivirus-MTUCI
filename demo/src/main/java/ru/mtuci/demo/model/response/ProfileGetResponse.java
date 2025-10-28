package ru.mtuci.demo.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mtuci.demo.model.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileGetResponse {

    private Long id;

    private String email;

    private String username;

    private Role role;

    private String status;
}
