package se.ifmo.origin_backend.utils;

import org.springframework.stereotype.Component;
import se.ifmo.origin_backend.dto.AddressDTO;
import se.ifmo.origin_backend.dto.CoordinatesDTO;
import se.ifmo.origin_backend.dto.LocationDTO;
import se.ifmo.origin_backend.dto.OrgFullDTO;
import se.ifmo.origin_backend.model.Organization;

@Component
public class OrgMapper {
    public OrgFullDTO orgToFullDto(Organization o) {
        return new OrgFullDTO(
                o.getName(),
                new CoordinatesDTO(o.getCoordinates().getX(), o.getCoordinates().getY()),
                o.getCreationDate(),
                new LocationDTO(o.getOfficialAddress().getX(), o.getOfficialAddress().getY(), o.getOfficialAddress().getZ(), o.getOfficialAddress().getName()),
                o.getAnnualTurnover(),
                o.getEmployeesCount(),
                o.getRating(),
                o.getType(),
                new AddressDTO(o.getPostalAddress().getStreet())
        );
    }
}
