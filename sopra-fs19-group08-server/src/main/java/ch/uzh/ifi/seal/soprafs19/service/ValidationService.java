package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.entity.Gameboard;
import ch.uzh.ifi.seal.soprafs19.entity.GodCards.GodCard;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.repository.GameboardRepository;
import ch.uzh.ifi.seal.soprafs19.repository.GodCardRepository;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs19.service.Exception.GameboardNotExistingException;
import ch.uzh.ifi.seal.soprafs19.service.Exception.PlayerNotExistingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ValidationService {

    private final PlayerRepository playerRepository;

    private final GameboardRepository gameboardRepository;

    private final GodCardRepository godCardRepository;

    @Autowired
    public ValidationService(@Qualifier("playerRepository") PlayerRepository playerRepository,
                             @Qualifier("gameboardRepository") GameboardRepository gameboardRepository,
                             @Qualifier("godCardRepository") GodCardRepository godCardRepository) {
        this.playerRepository = playerRepository;
        this.gameboardRepository = gameboardRepository;
        this.godCardRepository = godCardRepository;
    }

    public Gameboard getGameboardByPlayerId(Long playerId) { // checks if player exists and if there is already a gameboard, returns gameboard if existing
        Optional<Player> optionalPlayer = this.playerRepository.findById(playerId);
        if (optionalPlayer.isEmpty()) {
            throw new PlayerNotExistingException("This player does not exist!");
        }
        Player player = optionalPlayer.get();
        if (player.getGameId() != null){
            Gameboard gameboardById = this.gameboardRepository.findGameboardById(player.getGameId());
            if (gameboardById == null) {
                throw new GameboardNotExistingException("The gameboard doesn't not exist yet!");
            }
            else return gameboardById;
        }
        else throw new GameboardNotExistingException(("The gameboard doesn't exist yet!"));
    }

    public GodCard getGodcardByPlayerId(Long playerId) {
        return new GodCard();
    }
}
