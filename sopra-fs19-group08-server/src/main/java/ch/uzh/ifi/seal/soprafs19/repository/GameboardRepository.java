package ch.uzh.ifi.seal.soprafs19.repository;

import ch.uzh.ifi.seal.soprafs19.entity.Gameboard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("gameboardRepository")
public interface GameboardRepository extends CrudRepository<Gameboard, Long> {
    Gameboard findGameboardById(Long id);
}
