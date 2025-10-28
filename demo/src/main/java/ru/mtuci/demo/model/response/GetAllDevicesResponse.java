package ru.mtuci.demo.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mtuci.demo.model.Device;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllDevicesResponse {

    List<Device> devices;

    String status;

}
