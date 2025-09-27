package se.ifmo.origin_backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ifmo.origin_backend.dto.AddressDTO;
import se.ifmo.origin_backend.error.NotFoundElementWithIdException;
import se.ifmo.origin_backend.model.Address;
import se.ifmo.origin_backend.repo.AddressRepo;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {
    private static final Logger log = LoggerFactory.getLogger(AddressServiceTest.class);
    @Mock
    AddressRepo repo;

    @InjectMocks
    AddressService service;

    @BeforeEach
    void resetMocks() {
        clearInvocations(repo);
    }

    // helper
    private AddressDTO dto(String street) {
        // Adjust if your DTO is not a record: new AddressDTO(street);
        return new AddressDTO(street);
    }

    // getAddresses()
    @Test
    void getAddresses_returnsAll() {
//        log.info("test getAddresses_returnsAll started...");
        System.out.println("test getAddresses_returnsAll started...");
        Address a1 = new Address(); a1.setId(1L); a1.setStreet("A");
        Address a2 = new Address(); a2.setId(2L); a2.setStreet("B");
        when(repo.findAll()).thenReturn(List.of(a1, a2));

        List<Address> result = service.getAddresses();

        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getStreet());
        assertEquals("B", result.get(1).getStreet());
        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    // getAddressById()

    @Test
    void getAddressById_found_returnsEntity() {
        Address a = new Address(); a.setId(42L); a.setStreet("Main");
        when(repo.findById(42L)).thenReturn(Optional.of(a));

        Address result = service.getAddressById(42L);

        assertSame(a, result);
        verify(repo).findById(42L);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAddressById_notFound_throws() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundElementWithIdException.class,
                () -> service.getAddressById(99L));

        verify(repo).findById(99L);
        verifyNoMoreInteractions(repo);
    }

    // addAddress()

    @Test
    void addAddress_happyPath_mapsStreetAndSaves() {
        service.addAddress(dto("Elm Street"));

        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
        verify(repo).save(captor.capture());
        Address saved = captor.getValue();
        assertEquals("Elm Street", saved.getStreet());
        verifyNoMoreInteractions(repo);
    }

    // updateAddress()

    @Test
    void updateAddress_happyPath_mutatesExistingAndSavesSameInstance() {
        Address existing = new Address(); existing.setId(7L); existing.setStreet("Old");
        when(repo.findById(7L)).thenReturn(Optional.of(existing));

        service.updateAddress(7L, dto("New"));

        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
        verify(repo).findById(7L);
        verify(repo).save(captor.capture());
        Address saved = captor.getValue();

        assertSame(existing, saved);           // same managed instance
        assertEquals("New", existing.getStreet());
        verifyNoMoreInteractions(repo);
    }

    @Test
    void updateAddress_notFound_throwsAndDoesNotSave() {
        when(repo.findById(123L)).thenReturn(Optional.empty());

        assertThrows(NotFoundElementWithIdException.class,
                () -> service.updateAddress(123L, dto("X")));

        verify(repo).findById(123L);
        verify(repo, never()).save(any());
        verifyNoMoreInteractions(repo);
    }

    // deleteAddress()

    @Test
    void deleteAddress_found_deletesById() {
        when(repo.findById(5L)).thenReturn(Optional.of(new Address()));

        service.deleteAddress(5L);

        verify(repo).findById(5L);
        verify(repo).deleteById(5L);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAddress_notFound_throwsAndDoesNotDelete() {
        when(repo.findById(6L)).thenReturn(Optional.empty());

        assertThrows(NotFoundElementWithIdException.class,
                () -> service.deleteAddress(6L));

        verify(repo).findById(6L);
        verify(repo, never()).deleteById(anyLong());
        verifyNoMoreInteractions(repo);
    }

}
