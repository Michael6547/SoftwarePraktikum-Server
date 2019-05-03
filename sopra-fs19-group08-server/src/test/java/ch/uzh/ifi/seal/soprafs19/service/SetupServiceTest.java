package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.entity.*;
import ch.uzh.ifi.seal.soprafs19.entity.GodCards.Apollo;
import ch.uzh.ifi.seal.soprafs19.entity.GodCards.Artemis;
import ch.uzh.ifi.seal.soprafs19.entity.GodCards.GodCard;
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
public class SetupServiceTest {

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
    private SetupService setupService;

    @Autowired
    private PlayerService playerService;

    @Before
    public void setUp() throws Exception {

        // create two players
        Player player1 = new Player();
        player1.setPlayerName("player1");
        player1.setToken(UUID.randomUUID().toString());
        Player player2 = new Player();
        player2.setToken(UUID.randomUUID().toString());
        player2.setPlayerName("player2");
        Iterable<Player> testPlayers = playerRepository.findAll();
        playerRepository.save(player1);
        playerRepository.save(player2);


        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);


    }

    @After
    public void tearDown() throws Exception {
        fieldRepository.deleteAll();
        playerRepository.deleteAll();
        gameboardRepository.deleteAll();
        godCardRepository.deleteAll();
    }





    @Test
    public void createGameboard() {
        List<Player> players = new ArrayList<>();
        players.add(playerRepository.findByPlayerName("player1"));
        players.add(playerRepository.findByPlayerName("player2"));

        Long gameId = setupService.createGameboard(players);

        // test if gameboard exists
        Optional<Gameboard> optionalGameboard = gameboardRepository.findById(gameId);
        Assert.assertTrue("Gameboard was not created", optionalGameboard.isPresent());
        Gameboard gameboard = optionalGameboard.get();

        // test correct init of gameboard
        Assert.assertNotNull("gameboard does not exist", gameboard);
        Assert.assertNotNull("fields of gameboard do not exist", gameboard.getFields());
        Assert.assertNotNull("players are not assigned to the gameboard", gameboard.getPlayers());
    //    Assert.assertEquals("the players assigned are not the players that should have been assigned",
     //           players, gameboard.getPlayers()); // TODO: order should not matter
        Assert.assertEquals("the status is not set to 'Player1SelectGodcard'", GameStatus.Player1SelectGodcard, gameboard.getGameStatus());


        List<Field> fields = gameboard.getFields();
        Assert.assertEquals("there are not exactly 25 fields", 25, fields.size());

        // Test initialization of fields
        int x;
        for (x = 0; x<25; x++) {
            Field field = fields.get(x);
            Assert.assertEquals("the building level is not set to zero", new Integer(0), field.getBuildingLevel());
            Assert.assertFalse("the dome attribute should be set to false", field.getHasDome());
            Assert.assertNull("no player should be set to the field", field.getPlayerId());
        }
    }

    @Test
    public void setGodCards() {
        GodCard godCard1 = new Artemis();
        GodCard godCard2 = new Apollo();

        LoginRequest firstRequest = new LoginRequest();
        firstRequest.setPlayerName("testPlayer1");
        LoginRequest secondRequest = new LoginRequest();
        secondRequest.setPlayerName("testPlayer2");


        Long firstId = playerService.createPlayer(firstRequest).getPlayerId();
        Long secondId = playerService.createPlayer(secondRequest).getPlayerId();

        Player firstPlayer = this.playerRepository.findPlayerById(firstId);
        Player secondPlayer = this.playerRepository.findPlayerById(secondId);

        List<String> godCardList = new ArrayList<>();
        godCardList.add("Artemis");
        godCardList.add("Apollo");

        // player 1 selects two godcards
        if (firstPlayer.getPlayerStatus() == PlayerStatus.Player1) {
            this.setupService.setGodCards(firstPlayer.getId(), godCardList);
        } else {
            this.setupService.setGodCards(secondPlayer.getId(), godCardList);
        }

        Gameboard gameboard = gameboardRepository.findGameboardById(firstPlayer.getGameId());

        Assert.assertNotNull("the godcards should be assigned to the gameboard", gameboard.getGodCards());
        Assert.assertEquals("there should be exactly two god cards assigned", 2, gameboard.getGodCards().size());

        List<String> oneGodCard = new ArrayList<>();
        oneGodCard.add("Artemis");

        // player 1 selected two godcards, now player two needs to decide
        if (firstPlayer.getPlayerStatus() == PlayerStatus.Player1) {
            this.setupService.setGodCards(secondPlayer.getId(), oneGodCard);
        } else {
            this.setupService.setGodCards(firstPlayer.getId(), oneGodCard);
        }


        firstPlayer = playerRepository.findPlayerById(firstPlayer.getId());
        secondPlayer = playerRepository.findPlayerById(secondPlayer.getId());


        if (firstPlayer.getPlayerStatus() == PlayerStatus.Player1) {
            Assert.assertEquals("the wrong card is assigned to player1", "Apollo", firstPlayer.getGodCard().getName());
            Assert.assertEquals("the wrong card is assigned to player2", "Artemis",secondPlayer.getGodCard().getName());
        } else {
            Assert.assertEquals("the wrong card is assigned to player2", "Apollo", secondPlayer.getGodCard().getName());
            Assert.assertEquals("the wrong card is assigned to player1", "Artemis", firstPlayer.getGodCard().getName());
        }
    }

    @Test
    public void setWorker() {

        Player player1 = playerRepository.findByPlayerName("player1");
        Player player2 = playerRepository.findByPlayerName("player2");

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Long gameboardId = this.setupService.createGameboard(players);
        player1.setGameId(gameboardId);
        player2.setGameId(gameboardId);
        player1 = this.playerRepository.save(player1);
        player2 = this.playerRepository.save(player2);

        this.setupService.createGameboard(players);
        Field field = fieldRepository.findByXCoordinateAndYCoordinateAndGameId(2,2, gameboardId);

        List<String> godcards = new ArrayList<>();
        godcards.add("Basic");
        godcards.add("Basic");
        if (player1.getPlayerStatus() == PlayerStatus.Player1) {
            setupService.setGodCards(player1.getId(), godcards);
            setupService.setWorker(player1.getId(), field);
        } else {
            setupService.setGodCards(player2.getId(), godcards);
            setupService.setWorker(player2.getId(), field);
        }
        field = fieldRepository.findByXCoordinateAndYCoordinateAndGameId(2,2, gameboardId);

        Assert.assertEquals("player1 should be assigned to the field", player1.getId(), field.getPlayerId());


    }

}