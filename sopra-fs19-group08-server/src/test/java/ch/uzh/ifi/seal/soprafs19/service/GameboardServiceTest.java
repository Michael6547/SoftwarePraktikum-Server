package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.GameStatus;
import ch.uzh.ifi.seal.soprafs19.entity.Gameboard;
import ch.uzh.ifi.seal.soprafs19.entity.GodCards.GodCard;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.repository.FieldRepository;
import ch.uzh.ifi.seal.soprafs19.repository.GameboardRepository;
import ch.uzh.ifi.seal.soprafs19.repository.GodCardRepository;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class GameboardServiceTest {

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Qualifier("gameboardRepository")
    @Autowired
    private GameboardRepository gameboardRepository;

    @Qualifier("fieldRepository")
    @Autowired
    private FieldRepository fieldRepository;

    @Qualifier("godCardRepository")
    @Autowired
    private GodCardRepository godCardRepository;

    @Autowired
    private GameboardService gameboardService;


    @Before
    public void setUp() throws Exception {

        // create two players
        Player player1 = new Player();
        player1.setPlayerName("player1");
        player1.setToken(UUID.randomUUID().toString());
        GodCard godCard1 = new GodCard();
        godCard1 = godCardRepository.save(godCard1);
        player1.setGodCard(godCard1);
        Player player2 = new Player();
        player2.setToken(UUID.randomUUID().toString());
        GodCard godCard2 = new GodCard();
        godCard2 = godCardRepository.save(godCard2);
        player2.setGodCard(godCard2);
        player2.setPlayerName("player2");
        player1 = playerRepository.save(player1);
        player2 = playerRepository.save(player2);
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        // create 25 fields
        int x;
        int y;
        List<Field> fields = new ArrayList<>();
        for (x = 0; x<5; x++) {
            for (y = 0; y<5; y++) {
                Field field = new Field();
                field.setBuildingLevel(0);
                field.setHasDome(false);
                field.setxCoordinate(x);
                field.setyCoordinate(y);
                field = fieldRepository.save(field);
                fields.add(field);
            }
        }

        // create godcard list
        List<GodCard> godCardList = new ArrayList<>();
        godCardList.add(godCard1);
        godCardList.add(godCard2);

        // prepare gameboard
        Gameboard gameboard = new Gameboard();
        gameboard.setFields(fields);
        gameboard.setPlayers(players);
        gameboard.setGodCards(godCardList);
        gameboard.setGameStatus(GameStatus.Player1SelectGodcard);
        gameboard = gameboardRepository.save(gameboard);

        player1.setGameId(gameboard.getId());
        playerRepository.save(player1);
    }

    @After
    public void tearDown() throws Exception {
        fieldRepository.deleteAll();
        playerRepository.deleteAll();
        gameboardRepository.deleteAll();
        godCardRepository.deleteAll();
    }

    @Test
    @Ignore
    public void getValidMoves() {

    }

    @Test
    @Ignore
    public void performAction() {

    }

    @Test
    public void getCurrentGameboard() {
        Player player = playerRepository.findByPlayerName("player1");
        Optional<Gameboard> expectedGameboard = gameboardRepository.findById(player.getGameId());

        if (expectedGameboard.isEmpty()) {
            try {
                gameboardService.getCurrentGameboard(player.getId());
                Assert.fail("an error needs to be thrown if gameboard does not exist");
            } catch (Exception ex) {

            }
        } else {
            Gameboard gameboard = gameboardService.getCurrentGameboard(player.getId());
            Assert.assertEquals("stored gameboard does not match the expected gameboard", expectedGameboard.get().getId(), gameboard.getId());
        }




    }

    @Test
    public void delete() {
        Player player = playerRepository.findByPlayerName("player1");
        Player player2 = playerRepository.findByPlayerName("player1");
        Optional<Gameboard> expectedGameboard = gameboardRepository.findById(player.getGameId());

        Gameboard gameboard = expectedGameboard.get();
        this.gameboardService.delete(player.getId());


        Assert.assertNull("Player should not exist anymore", playerRepository.findPlayerById(player.getId()));
        Assert.assertNull("Godcard should not exist anymore", godCardRepository.findGodCardById(player.getGodCard().getId()));

        Assert.assertEquals("Only one player should be left", 1, gameboard.getPlayers().size());
        Assert.assertEquals("Only one godcard should be left", 1, gameboard.getGodCards().size());
        Assert.assertEquals(GameStatus.PlayerRemoved, gameboard.getGameStatus());

        this.gameboardService.delete(player2.getId());



        Assert.assertNull("Player should not exist anymore", playerRepository.findPlayerById(player2.getId()));
        Assert.assertNull("Godcard should not exist anymore", godCardRepository.findGodCardById(player2.getGodCard().getId()));
        Assert.assertNull("Gameboard should not exist anymore", gameboardRepository.findGameboardById(gameboard.getId()));

    }
}