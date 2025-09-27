package se.ifmo.origin_backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import se.ifmo.origin_backend.dto.OrgCreateDTO;
import se.ifmo.origin_backend.dto.OrgFullDTO;
import se.ifmo.origin_backend.error.NotFoundElementWithIdException;
import se.ifmo.origin_backend.model.*;
import se.ifmo.origin_backend.repo.AddressRepo;
import se.ifmo.origin_backend.repo.CoordinatesRepo;
import se.ifmo.origin_backend.repo.LocationRepo;
import se.ifmo.origin_backend.repo.OrganizationRepo;
import se.ifmo.origin_backend.utils.OrgMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock OrganizationRepo orgRepo;
    @Mock CoordinatesRepo cordRepo;
    @Mock AddressRepo addrRepo;
    @Mock LocationRepo locRepo;
    OrgMapper orgMapper = new OrgMapper();

    OrganizationService service;

    @BeforeEach
    void setUp() {
        service = new OrganizationService(orgRepo, cordRepo, addrRepo, locRepo);
    }

    // Helpers
    private OrgCreateDTO dto(long coordId, long locId, long postalAddrId) {
        return new OrgCreateDTO(
                "Acme", coordId, locId, postalAddrId,
                /*annualTurnover*/ 10L, /*employees*/ 5L, /* rating */ 3,
                /*type*/ OrganizationType.PUBLIC
        );
    }

    // getById
    @Test
    void getOrganizationById_found() {
        Coordinates cords = new Coordinates(1L, 1L, 1L);
        Location loc = new Location(1L, 2, 1, 2, "name");
        Address adr = new Address(1L, "street");
        Organization o = new Organization(); o.setId(1); o.setCoordinates(cords); o.setOfficialAddress(loc); o.setPostalAddress(adr);
        var dto = orgMapper.orgToFullDto(o);
        when(orgRepo.findFullById(1)).thenReturn(Optional.of(dto));

        OrgFullDTO get = service.getOrganizationById(1);

        assertSame(get, dto);
        assertSame(get, dto);
    }

    @Test
    void getOrganizationById_notFound() {
        when(orgRepo.findFullById(42)).thenReturn(Optional.empty());
        assertThrows(NotFoundElementWithIdException.class,
                () -> service.getOrganizationById(42));
    }

    // add

    @Test
    void addOrganization_happyPath() {
        // Arrange: refs exist
        Coordinates c = new Coordinates(1L, 1L, 2L);
        Location loc = new Location(20L, 3f, 1, 4f, "name");
        Address postal = new Address(300L, "PO Box");

        when(cordRepo.findById(1L)).thenReturn(Optional.of(c));
        when(locRepo.findById(20L)).thenReturn(Optional.of(loc));
        when(addrRepo.findById(300L)).thenReturn(Optional.of(postal));

        // Act
        service.addOrganization(dto(1L, 20L, 300L));

        // Assert: save called with mapped entity
        ArgumentCaptor<Organization> cap = ArgumentCaptor.forClass(Organization.class);
        verify(orgRepo).save(cap.capture());
        Organization saved = cap.getValue();
        assertEquals("Acme", saved.getName());
        assertSame(c, saved.getCoordinates());
        assertSame(loc, saved.getOfficialAddress());
        assertSame(postal, saved.getPostalAddress());
        assertEquals(10L, saved.getAnnualTurnover());
        assertEquals(5L, saved.getEmployeesCount());
        assertEquals(3, saved.getRating());
        assertEquals(OrganizationType.PUBLIC, saved.getType());
    }

    @Test
    void addOrganization_missingCoordinates_throwsAndDoesNotSave() {
        when(cordRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundElementWithIdException.class,
                () -> service.addOrganization(dto(999L, 200L, 300L)));

        verify(orgRepo, never()).save(any());
    }

    // update

    @Test
    void updateOrganization_mutatesExistingAndSavesSameInstance() {
        Organization existing = new Organization(); existing.setId(7);
        when(orgRepo.findById(7)).thenReturn(Optional.of(existing));

        Coordinates c = new Coordinates(1L, 1L, 2L);
        Location loc = new Location(20L, 3f, 1, 4f, "name");
        Address postal = new Address(1L, "PO Box");
        when(cordRepo.findById(1L)).thenReturn(Optional.of(c));
        when(locRepo.findById(20L)).thenReturn(Optional.of(loc));
        when(addrRepo.findById(1L)).thenReturn(Optional.of(postal));

        service.updateOrganization(7, dto(1L, 20L, 1L));

        ArgumentCaptor<Organization> cap = ArgumentCaptor.forClass(Organization.class);
        verify(orgRepo).save(cap.capture());
        assertSame(existing, cap.getValue()); // same instance saved
        assertSame(c, existing.getCoordinates());
        assertSame(loc, existing.getOfficialAddress());
        assertSame(postal, existing.getPostalAddress());
    }

    // delete

    @Test
    void deleteOrganization_notFound_throws() {
        when(orgRepo.findById(11)).thenReturn(Optional.empty());
        assertThrows(NotFoundElementWithIdException.class,
                () -> service.deleteOrganization(11));
        verify(orgRepo, never()).deleteById(anyInt());
    }

    // clear

    @Test
    void clear_deletesAllRepositories() {
        service.clear();
        verify(orgRepo).deleteAll();
        verify(cordRepo).deleteAll();
        verify(addrRepo).deleteAll();
        verify(locRepo).deleteAll();
    }
}

