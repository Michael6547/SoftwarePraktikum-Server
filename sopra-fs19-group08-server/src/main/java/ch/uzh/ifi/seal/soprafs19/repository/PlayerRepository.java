package ch.uzh.ifi.seal.soprafs19.repository;

import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.entity.PlayerStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("playerRepository")
public interface PlayerRepository extends CrudRepository<Player, Long> {
	Player findByPlayerName(String playerName);
	Player findByToken(String token);
	Player findByGameIdIsNull();
	Player findPlayerById(Long playerId);
	Player findByPlayerStatus(PlayerStatus playerStatus);
}
