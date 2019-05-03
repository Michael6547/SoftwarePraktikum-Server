package ch.uzh.ifi.seal.soprafs19.repository;

import ch.uzh.ifi.seal.soprafs19.entity.Gameboard;
import ch.uzh.ifi.seal.soprafs19.entity.GodCards.GodCard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("godCardRepository")
public interface GodCardRepository extends CrudRepository<GodCard, Long> {
    GodCard findGodCardById(Long id);
}
