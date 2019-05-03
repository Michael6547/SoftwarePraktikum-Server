package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.entity.GodCards.GodCard;
import ch.uzh.ifi.seal.soprafs19.entity.LoginRequest;
import ch.uzh.ifi.seal.soprafs19.entity.LoginResponse;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.repository.GodCardRepository;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs19.service.Exception.PlayerAlreadyExistingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;

    private final GodCardRepository godCardRepository;

    private final SetupService setupService;


    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository,
                         @Qualifier("godCardRepository") GodCardRepository godCardRepository,
                         @Qualifier("setupService") SetupService setupService) {
        this.playerRepository = playerRepository;
        this.godCardRepository = godCardRepository;
        this.setupService = setupService;
    }

    public LoginResponse createPlayer(LoginRequest loginRequest) {
        if (playerRepository.findByPlayerName(loginRequest.getPlayerName()) != null){
            throw new PlayerAlreadyExistingException("This player already exists!");
        }
        else {
            Player newPlayer = new Player();
            newPlayer.setPlayerName(loginRequest.getPlayerName());
            newPlayer.setToken(UUID.randomUUID().toString());
            GodCard godCard = new GodCard();
            godCard = godCardRepository.save(godCard);
            newPlayer.setGodCard(godCard);
            newPlayer = playerRepository.save(newPlayer);
            // it is checked if there is an unmatched player in the repository
            newPlayer = this.setupService.matchPlayer(newPlayer);
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setPlayerId(newPlayer.getId());
            return loginResponse;
        }
    }
}
