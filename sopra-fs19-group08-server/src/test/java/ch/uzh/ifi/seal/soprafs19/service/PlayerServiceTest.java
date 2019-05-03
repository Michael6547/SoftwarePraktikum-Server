package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.entity.LoginRequest;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Test class for the PlayerResource REST resource.
 *
 * @see PlayerService
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class PlayerServiceTest {


    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerService playerService;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void createPlayer() {
        Assert.assertNull("no player with name 'testPlayerName' should exist before creating it", playerRepository.findByPlayerName("testPlayerName"));

        Player testPlayer = new Player();
        testPlayer.setPlayerName("testPlayerName");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPlayerName("testPlayerName");

        this.playerService.createPlayer(loginRequest);

        Assert.assertNotNull("after creating there should be an instance of player", playerRepository.findByPlayerName("testPlayerName"));
        Player createdPlayer = playerRepository.findByPlayerName("testPlayerName");
        Assert.assertNotNull("the player should have a token set", createdPlayer.getToken());

        try {
            this.playerService.createPlayer(loginRequest);
            Assert.fail("it is not possible to add more than one player with the same name");
        } catch (Exception ex) {
        }
    }

}
