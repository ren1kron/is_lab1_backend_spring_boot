package se.ifmo.origin_backend.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import se.ifmo.origin_backend.dto.CoordinatesDTO;
import se.ifmo.origin_backend.error.NotFoundElementWithIdException;
import se.ifmo.origin_backend.model.Coordinates;
import se.ifmo.origin_backend.repo.CoordinatesRepo;

import java.util.List;

@Service
@AllArgsConstructor
public class CoordinatesService {

    private final CoordinatesRepo repo;

    public List<Coordinates> getAllCoordinates() {
        return repo.findAll();
    }

    public Coordinates getCoordinatesById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundElementWithIdException("Coordinates", id));
    }

    @Transactional
    public void addCoordinates(CoordinatesDTO dto) {
        var cords = new Coordinates();
        cords.setX(dto.x());
        cords.setY(dto.y());
        repo.save(cords);
    }

    @Transactional
    public void updateCoords(long id, CoordinatesDTO dto) {
        var cords = repo.findById(id)
                .orElseThrow(() -> new NotFoundElementWithIdException("Coordinates", id));

        cords.setX(dto.x());
        cords.setY(dto.y());
        repo.save(cords);
    }

    @Transactional
    public void deleteCoordinates(Long id) {
        if (repo.findById(id).isEmpty()) throw new NotFoundElementWithIdException("Coordinates", id);
        repo.deleteById(id);
    }
}
