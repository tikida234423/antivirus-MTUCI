package ru.mtuci.demo.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mtuci.demo.model.Ticket;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LicenseInfoResponse {

    private Ticket ticket;

    private String status;

}
