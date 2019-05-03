package ch.uzh.ifi.seal.soprafs19.repository;

import ch.uzh.ifi.seal.soprafs19.entity.Actions.Action;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository("actionRepository")
public interface ActionRepository extends CrudRepository<Action, Long> {
}