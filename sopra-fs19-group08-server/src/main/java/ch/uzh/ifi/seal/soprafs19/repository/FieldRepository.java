package ch.uzh.ifi.seal.soprafs19.repository;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("fieldRepository")
public interface FieldRepository extends CrudRepository<Field, Long> {
    Field findByXCoordinateAndYCoordinateAndGameId(Integer x, Integer y, Long gameId);
    List<Field> findAllByGameId(Long gameId);
}
