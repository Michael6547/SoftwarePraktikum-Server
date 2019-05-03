package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.entity.Gameboard;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.repository.GameboardRepository;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.UUID;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class ValidationServiceTest {

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Qualifier("gameboardRepository")
    @Autowired
    private GameboardRepository gameboardRepository;

    @Autowired
    private ValidationService validationService;

    @Test
    public void getGameboardByPlayerId() {

        try {
            // player does not exist, method call should throw error
            validationService.getGameboardByPlayerId(new Long("123"));
            Assert.fail("If player does not exist, method should throw an error");
        } catch (Exception ex) {
        }

        Player newPlayer = new Player();
        newPlayer.setPlayerName("player");
        newPlayer.setToken(UUID.randomUUID().toString());
        newPlayer = playerRepository.save(newPlayer);

        try {
            // gameboard does not exist, method call should throw error
            validationService.getGameboardByPlayerId(newPlayer.getId());
            Assert.fail("If gameboard does not exist, or is not assigned to the player, the method should throw an error.");
        } catch (Exception ex) {

        }

        Gameboard gameboard = new Gameboard();
        gameboard = gameboardRepository.save(gameboard);

        newPlayer.setGameId(gameboard.getId());
        playerRepository.save(newPlayer);

        try {
            validationService.getGameboardByPlayerId(newPlayer.getId());
        } catch (Exception ex) {
            Assert.fail("gameboard is assigned to player, method should return gameId");
        }


    }
}