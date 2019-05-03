package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.entity.*;
import ch.uzh.ifi.seal.soprafs19.entity.GodCards.*;
import ch.uzh.ifi.seal.soprafs19.repository.FieldRepository;
import ch.uzh.ifi.seal.soprafs19.repository.GameboardRepository;
import ch.uzh.ifi.seal.soprafs19.repository.GodCardRepository;
import ch.uzh.ifi.seal.soprafs19.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs19.service.Exception.ActionNotPossibleException;
import org.aspectj.apache.bcel.util.Play;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class SetupService {

    private final PlayerRepository playerRepository;
    private final GameboardRepository gameboardRepository;
    private final FieldRepository fieldRepository;
    private final ValidationService validationService;
    private final GodCardRepository godCardRepository;


    @Autowired
    public SetupService(@Qualifier("playerRepository") PlayerRepository playerRepository,
                        @Qualifier("gameboardRepository") GameboardRepository gameboardRepository,
                        @Qualifier("fieldRepository") FieldRepository fieldRepository,
                        @Qualifier("godCardRepository") GodCardRepository godCardRepository,
                        ValidationService validationService)  {
        this.playerRepository = playerRepository;
        this.gameboardRepository = gameboardRepository;
        this.fieldRepository = fieldRepository;
        this.validationService = validationService;
        this.godCardRepository = godCardRepository;
    }


    public void setGodCards(Long playerId, List<String> godCards) {
        if (godCards.get(0).equals("Basic") && godCards.size() == 2){
            Gameboard gameboard = this.validationService.getGameboardByPlayerId(playerId);
            gameboard.setGameStatus(GameStatus.Player1SetWorkers);
            gameboard = gameboardRepository.save(gameboard);
        }
        else if (godCards.get(0).equals("Basic") && godCards.size() == 1){
            Gameboard gameboard = this.validationService.getGameboardByPlayerId(playerId);
            gameboard.setGameStatus(GameStatus.Player1SetWorkers);
            gameboard = gameboardRepository.save(gameboard);
        }
        else {
            // TODO: throw error if called after first worker is set
            Gameboard gameboard = this.validationService.getGameboardByPlayerId(playerId);
            Player player = playerRepository.findPlayerById(playerId);
            if (gameboard.getGameStatus() != GameStatus.Player1SelectGodcard && gameboard.getGameStatus() != GameStatus.Player2SelectGodcard) {
                throw new ActionNotPossibleException("Cannot perform action, wrong gamestatus");
            }
            if (godCards.size() == 2) {
                if (player.getPlayerStatus() != PlayerStatus.Player1) {
                    throw new ActionNotPossibleException("Player 1 needs to choose the two godcards.");
                }
                if (gameboard.getGodCards() != null && gameboard.getGodCards().size() != 0) {
                    throw new ActionNotPossibleException("The two godcards are already selected");
                }
                List<GodCard> godCardList = new ArrayList<>();
                int index = 0;
                for (index = 0; index < godCards.size(); index++) {
                    switch (godCards.get(index)) {
                        case "Apollo":
                            godCardList.add(new Apollo());
                            break;
                        case "Artemis":
                            godCardList.add(new Artemis());
                            break;
                        case "Athena":
                            godCardList.add(new Athena());
                            break;
                        case "Atlas":
                            godCardList.add(new Atlas());
                            break;
                        case "Demeter":
                            godCardList.add(new Demeter());
                            break;
                        case "Hephaestus":
                            godCardList.add(new Hephaestus());
                            break;
                        case "Hermes":
                            godCardList.add(new Hermes());
                            break;
                        case "Minotaur":
                            godCardList.add(new Minotaur());
                            break;
                        case "Pan":
                            godCardList.add(new Pan());
                            break;
                        case "Prometheus":
                            godCardList.add(new Prometheus());
                            break;
                        default:
                            throw new ActionNotPossibleException("The selected godcard does not exist.");
                    }
                    godCardRepository.save(godCardList.get(index));
                }
                gameboard.setGodCards(godCardList);
                gameboard.setGameStatus(GameStatus.Player2SelectGodcard);
                gameboard = gameboardRepository.save(gameboard);
            } else if (godCards.size() == 1) {
                if (gameboard.getGodCards() == null) {
                    throw new ActionNotPossibleException("Let player 1 select two godcards first");
                }
                if (player.getPlayerStatus() != PlayerStatus.Player2) {
                    throw new ActionNotPossibleException("Only player 2 can select the godcard.");
                }
            /*gameboard.getPlayers().forEach(player -> {
                if (player.getGodCard() != ) {
                    throw new ActionNotPossibleException("Godcards are already selected");
                }
            });*/

                String nameFirstGodcard = godCards.get(0);
                if (!gameboard.getGodCards().get(0).getName().equals(nameFirstGodcard) && !gameboard.getGodCards().get(1).getName().equals(nameFirstGodcard)) { // Godcard is not in the selected godcards
                    throw new ActionNotPossibleException("Godcard is not in the godcards that can be selected");
                }
                GodCard firstGodCard = null;
                GodCard secondGodCard = null;
                Iterator<GodCard> iterator = gameboard.getGodCards().iterator();
                while (iterator.hasNext()) {
                    GodCard iteratorGodCard = iterator.next();
                    if (!iteratorGodCard.getName().equals(nameFirstGodcard)) {
                        secondGodCard = iteratorGodCard;
                    } else firstGodCard = iteratorGodCard;
                }
                if (secondGodCard == null) {
                    throw new ActionNotPossibleException("Error occured while assigning godcards");
                }
                if (player.getGodCard() != null && !player.getGodCard().getName().equals("Basic")) { // Player already has a god card
                    throw new ActionNotPossibleException("Godcard already selected");
                }
                player.setGodCard(firstGodCard);
                player = playerRepository.save(player);
                int i = 0;
                for (i = 0; i < gameboard.getPlayers().size(); i++) {
                    if (gameboard.getPlayers().get(i).getId() != player.getId()) {
                        Player secondPlayer = gameboard.getPlayers().get(i);
                        secondPlayer.setGodCard(secondGodCard);
                        secondPlayer = playerRepository.save(secondPlayer);
                    }
                }
                gameboard.setGameStatus(GameStatus.Player1SetWorkers);
                gameboard = gameboardRepository.save(gameboard);
            } else {
                throw new ActionNotPossibleException("Incorrect number of godcards");
            }
        }
    }

    public void setWorker(Long playerId, Field field) {

        Gameboard gameboard = this.validationService.getGameboardByPlayerId(playerId);
        Player player = this.playerRepository.findById(playerId).get(); // player does exist, because validationService checked it
        List<Field> fields = gameboard.getFields();
        Field newField = this.fieldRepository.findByXCoordinateAndYCoordinateAndGameId(field.getxCoordinate(), field.getyCoordinate(), gameboard.getId());

        if (!(gameboard.getGameStatus() == GameStatus.Player1SetWorkers && player.getPlayerStatus() == PlayerStatus.Player1)
                && !(gameboard.getGameStatus() == GameStatus.Player2SetWorkers && player.getPlayerStatus() == PlayerStatus.Player2)
        ) {
            throw new ActionNotPossibleException("Not possible to set workers. Wrong gamestatus or wrong player.");
        }

        int i = 0;
        int count = 0;
        for (i = 0; i < fields.size(); i++) {
            Long playerFieldId = fields.get(i).getPlayerId();
            if (playerFieldId != null && playerFieldId == playerId) {
                count++;
            }
            if (fields.get(i) == newField && fields.get(i).getPlayerId() != null) {
                throw new ActionNotPossibleException("Field is already occupied");
            }
        }
        if (count > 1) {
            throw new ActionNotPossibleException("The workers are already set");
        }
        if (count == 1 ) {
            if (player.getPlayerStatus() == PlayerStatus.Player1) {
                gameboard.setGameStatus(GameStatus.Player2SetWorkers);
            } else {
                gameboard.setGameStatus(GameStatus.Player1Turn);
            }
            gameboard = gameboardRepository.save(gameboard);
        }
        newField.setPlayerId(player.getId());
        this.fieldRepository.save(newField);
    }

    public Player matchPlayer(Player newPlayer){
        Player unmatchedPlayer = this.playerRepository.findByPlayerStatus(PlayerStatus.Waiting);
        if (unmatchedPlayer != null) {
            List<Player> playerList = new ArrayList<>();
            //TODO: reactivate randomizer after testing is done
         //   if (Math.random() < 0.5) {
           //     newPlayer.setPlayerStatus(PlayerStatus.Player1);
             //   unmatchedPlayer.setPlayerStatus(PlayerStatus.Player2);
           // } else {
                newPlayer.setPlayerStatus(PlayerStatus.Player2);
                unmatchedPlayer.setPlayerStatus(PlayerStatus.Player1);
            //}
            playerList.add(newPlayer);
            // unmatchedPlayer = playerRepository.save(unmatchedPlayer);
            playerList.add(unmatchedPlayer);

            Long gameId = this.createGameboard(playerList);

            unmatchedPlayer.setGameId(gameId);
            newPlayer.setGameId(gameId);
            unmatchedPlayer = playerRepository.save(unmatchedPlayer);
            newPlayer = playerRepository.save(newPlayer);
            newPlayer.setGameId(gameId);
        } else {
            newPlayer.setPlayerStatus(PlayerStatus.Waiting);
            newPlayer = playerRepository.save(newPlayer);
        }
        return newPlayer;
    }
    //the pairing is done in this class whenever a new Player is created

    //The gameboard is created with both players and all the empty fields
    public Long createGameboard(List<Player> players) { // creates a gameboard instance with the players, stores it and returns the gameId
        Gameboard gameboard = new Gameboard();
        gameboard.setPlayers(players);

        gameboard = gameboardRepository.save(gameboard);

        Iterable<Gameboard> gameboardIterator = gameboardRepository.findAll();
        List<Field> fieldList = this.setFields(gameboard.getId());
        gameboard.setFields(fieldList);
        gameboard.setGameStatus(GameStatus.Player1SelectGodcard);

        gameboard = gameboardRepository.save(gameboard);
        gameboardIterator = gameboardRepository.findAll();

        return gameboard.getId();
    }

    //the instance of every Field is created
    public List<Field> setFields(Long gameboardId){
        List<Field> fieldList = new ArrayList<>();
        for (int i = 0; i<5; i++){
            for (int k = 0; k<5; k++){
                Field newField = new Field();
                newField.setxCoordinate(k);
                newField.setyCoordinate(i);
                newField.setBuildingLevel(0);
                newField.setHasDome(false);
                newField.setGameId(gameboardId);
                fieldList.add(newField);
                this.fieldRepository.save(newField);
            }
        }



        return fieldList;
    }

    public Iterable<Gameboard> getAllGameboards() {
        return this.gameboardRepository.findAll();
    }

    public Iterable<Player> getAllPlayers() {
        return this.playerRepository.findAll();
    }
}
