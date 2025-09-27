package se.ifmo.origin_backend.dto;

import se.ifmo.origin_backend.model.OrganizationType;

import java.time.LocalDate;

public record OrgFullDTO(
        String name,
        CoordinatesDTO coordinates,
        LocalDate creationDate,
        LocationDTO officialAddress,
        Long annualTurnover,
        Long employeesCount,
        int rating,
        OrganizationType type,
        AddressDTO postalAddress
) { }
