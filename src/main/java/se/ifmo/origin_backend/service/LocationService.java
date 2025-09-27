package se.ifmo.origin_backend.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import se.ifmo.origin_backend.dto.LocationDTO;
import se.ifmo.origin_backend.error.NotFoundElementWithIdException;
import se.ifmo.origin_backend.model.Location;
import se.ifmo.origin_backend.repo.LocationRepo;

import java.util.List;

@Service
@AllArgsConstructor
public class LocationService {

    private final LocationRepo repo;

    public List<Location> getLocations() {
        return repo.findAll();
    }

    public Location getLocationById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundElementWithIdException("Location", id));
    }

    @Transactional
    public void addLocation(LocationDTO dto) {
        var loc = new Location();
        loc.setX(dto.x());
        loc.setY(dto.y());
        loc.setZ(dto.z());
        loc.setName(dto.name());

        repo.save(loc);
    }

    @Transactional
    public void updateLocation(Long id, LocationDTO dto) {
        var loc = repo.findById(id)
                .orElseThrow(() -> new NotFoundElementWithIdException("Location", id));

        loc.setX(dto.x());
        loc.setY(dto.y());
        loc.setZ(dto.z());
        loc.setName(dto.name());

        repo.save(loc);
    }

    @Transactional
    public void deleteLocation(Long id) {
        if (repo.findById(id).isEmpty())
            throw new NotFoundElementWithIdException("Location", id);
        repo.deleteById(id);
    }
}
