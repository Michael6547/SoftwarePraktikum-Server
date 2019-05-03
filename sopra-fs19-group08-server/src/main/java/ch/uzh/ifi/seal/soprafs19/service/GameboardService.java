package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.entity.*;
import ch.uzh.ifi.seal.soprafs19.entity.Actions.*;
import ch.uzh.ifi.seal.soprafs19.entity.GodCards.GodCard;
import ch.uzh.ifi.seal.soprafs19.repository.*;
import ch.uzh.ifi.seal.soprafs19.service.Exception.ActionNotPossibleException;
import org.aspectj.apache.bcel.util.Play;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GameboardService {

    private final PlayerRepository playerRepository;

    private final GameboardRepository gameboardRepository;

    private final ValidationService validationService;

    private final FieldRepository fieldRepository;

    private final ActionRepository actionRepository;

    private final GodCardRepository godCardRepository;

    @Autowired
    public GameboardService(@Qualifier("playerRepository") PlayerRepository playerRepository,
                            @Qualifier("gameboardRepository") GameboardRepository gameboardRepository,
                            @Qualifier("validationService") ValidationService validationService,
                            @Qualifier("fieldRepository") FieldRepository fieldRepository,
                            @Qualifier("actionRepository") ActionRepository actionRepository,
                            @Qualifier("godCardRepository") GodCardRepository godCardRepository) {
        this.playerRepository = playerRepository;
        this.gameboardRepository = gameboardRepository;
        this.validationService = validationService;
        this.fieldRepository = fieldRepository;
        this.actionRepository = actionRepository;
        this.godCardRepository = godCardRepository;
    }

    public ValidMoves getValidMoves(Long playerId, RequestMoves requestMoves, Integer enemyPushable) {
        Gameboard gameboard = validationService.getGameboardByPlayerId(playerId);
        if (gameboard.getGameStatus() != GameStatus.Player1Turn && gameboard.getGameStatus() != GameStatus.Player2Turn) {
            throw new ActionNotPossibleException("Cannot perform action, wrong gamestatus");
        }
        Player player = playerRepository.findById(playerId).get();
        if (!(gameboard.getGameStatus() == GameStatus.Player1Turn && player.getPlayerStatus() == PlayerStatus.Player1)
                && !(gameboard.getGameStatus() == GameStatus.Player2Turn && player.getPlayerStatus() == PlayerStatus.Player2)
        ) {
            throw new ActionNotPossibleException("It is not your turn");
        }

        GodCard godCard = player.getGodCard();


        // if new turn starts, delete information from last turn
        if (godCard.getLastTurn() != null && godCard.getLastTurn().size() > 0 && godCard.getLastTurn().get(godCard.getLastTurn().size() - 1).getTypeOfAction() == TypeOfAction.EndTurn) {
            godCard.setLastTurn(new ArrayList<>());
            godCardRepository.save(godCard);
        }

        Field fromField = getFieldFromRepository(requestMoves.getFromField(), gameboard.getId());
        Field toField = getFieldFromRepository(requestMoves.getToField(), gameboard.getId());

        // ensure player is on from field (exclude case EndTurn where fields are null)
        if (fromField != null && toField != null &&
                (fromField.getPlayerId() == null || !fromField.getPlayerId().equals(playerId))) {
            throw new ActionNotPossibleException("Player does not have a worker on chosen field");
        }
        //checks if player can be pushed for the minotaur godcard
        if (player.getGodCard().getName().equals("Minotaur")){
            ValidMoves validMoves = player.getGodCard().getValidMoves(fromField, toField, isEnemyPushable(playerId, requestMoves));
            return validMoves;
        }
        // method gets valid fields
        else {
            ValidMoves validMoves = player.getGodCard().getValidMoves(fromField, toField, 0);
            return validMoves;
        }

    }

    public Integer isEnemyPushable(Long playerId, RequestMoves requestMoves){
        //Integer enemyPushable = 0;
        Gameboard gameboard = validationService.getGameboardByPlayerId(playerId);
        Player player = playerRepository.findById(playerId).get();
        Field fromField = getFieldFromRepository(requestMoves.getFromField(), gameboard.getId());
        Field toField = getFieldFromRepository(requestMoves.getToField(), gameboard.getId());
        Field targetField = fieldRepository.findByXCoordinateAndYCoordinateAndGameId(toField.getxCoordinate()-fromField.getxCoordinate()+toField.getxCoordinate(),
                toField.getyCoordinate()-fromField.getyCoordinate()+toField.getyCoordinate(), gameboard.getId());

        //multiple nested if's for better overview.. can be combined with && later
        if (toField.getPlayerId() != playerId && toField.getPlayerId() != null){
            if (!(toField.getxCoordinate() != fromField.getxCoordinate()
                    && toField.getyCoordinate() != fromField.getyCoordinate())){
                if (targetField.getxCoordinate() < 5 && targetField.getxCoordinate() >= 0
                        && targetField.getyCoordinate() < 5 && targetField.getyCoordinate() >= 0
                    && targetField.getBuildingLevel() - toField.getBuildingLevel() <= 0) {
                    //enemyPushable = 1;
                    return 1;
                }
            }
        }


        return 0;
    }

    public Gameboard performAction(Long playerId, Action action) {
        Gameboard gameboard = validationService.getGameboardByPlayerId(playerId);
        Long gameId = gameboard.getId();
        if (gameboard.getGameStatus() != GameStatus.Player1Turn && gameboard.getGameStatus() != GameStatus.Player2Turn) {
            throw new ActionNotPossibleException("Cannot perform action, wrong gamestatus");
        }
        RequestMoves requestMoves = new RequestMoves();
        requestMoves.setFromField(action.getFromField());
        requestMoves.setToField(action.getToField());

        Player player = playerRepository.findById(playerId).get();
        Field fromField = getFieldFromRepository(action.getFromField(), gameId);
        Field toField = getFieldFromRepository(action.getToField(), gameId);

        Integer enemyPushable = 0; //if checker is 0 in godcard Minotaur, player cannot be pushed
        // validate that the chosen action is among valid moves
        ValidMoves validMoves = getValidMoves(playerId, requestMoves, enemyPushable);
        if (validMoves.getValidActions() == null || validMoves.getValidActions().size() == 0
                && action.getTypeOfAction() != TypeOfAction.EndTurn) {
            throw new ActionNotPossibleException("The action is not valid.");
        }

        Integer valid = 0;
        for (int i = 0; i < validMoves.getValidActions().size(); i++) {
            if (validMoves.getValidActions().get(i).getTypeOfAction() == action.getTypeOfAction()
                    && validMoves.getValidActions().get(i).getFromField().getxCoordinate() == action.getFromField().getxCoordinate()
                    &&validMoves.getValidActions().get(i).getFromField().getyCoordinate() == action.getFromField().getyCoordinate()
                    && validMoves.getValidActions().get(i).getToField().getxCoordinate() == action.getToField().getxCoordinate()
                    && validMoves.getValidActions().get(i).getToField().getyCoordinate() == action.getToField().getyCoordinate()) {
                valid = 1;
                break;
            }
            else if (action.getTypeOfAction() == TypeOfAction.EndTurn){
                valid = 1;
                break;
            }
        }

        if (valid == 0) {
            throw new ActionNotPossibleException("The action is not among the valid actions");
        }


        action.setFromField(fromField);
        action.setToField(toField);
        action = actionRepository.save(action);

        //Build action
        if (action.getTypeOfAction() == TypeOfAction.Build){
            Field newField = action.getToField();
            Field oldField = fieldRepository.findByXCoordinateAndYCoordinateAndGameId(newField.getxCoordinate(), newField.getyCoordinate(), gameId);
            oldField.setBuildingLevel(oldField.getBuildingLevel() + 1);
            fieldRepository.save(oldField);

        } else if (action.getTypeOfAction() == TypeOfAction.Move){ //Move action

            Field oldFromField = fieldRepository.findByXCoordinateAndYCoordinateAndGameId(action.getFromField().getxCoordinate(), action.getFromField().getyCoordinate(), gameId);
            Field oldToField = fieldRepository.findByXCoordinateAndYCoordinateAndGameId(toField.getxCoordinate(), toField.getyCoordinate(), gameId);
            Long enemyPlayerId = oldToField.getPlayerId();

            //This part is specifically for the Godcard Minotaur
            RequestMoves requestMoves1 = new RequestMoves();
            requestMoves1.setFromField(oldFromField);
            requestMoves1.setToField(oldToField);
            if (player.getGodCard().getName().equals("Minotaur") && isEnemyPushable(player.getId(), requestMoves) == 1){
                Field targetField = fieldRepository.findByXCoordinateAndYCoordinateAndGameId(toField.getxCoordinate()-fromField.getxCoordinate()+toField.getxCoordinate(),
                        toField.getyCoordinate()-fromField.getyCoordinate()+toField.getyCoordinate(), gameboard.getId());
                targetField.setPlayerId(enemyPlayerId);
                fieldRepository.save(targetField);
            }
            // end part for Minotaur

            oldToField.setPlayerId(player.getId());
            oldFromField.setPlayerId(null);


            fieldRepository.save(oldFromField);
            fieldRepository.save(oldToField);

        } else if (action.getTypeOfAction() == TypeOfAction.EndTurn){
            if (gameboard.getGameStatus() == GameStatus.Player1Turn){
                gameboard.setGameStatus(GameStatus.Player2Turn);
            }
            else if (gameboard.getGameStatus() == GameStatus.Player2Turn){
                gameboard.setGameStatus(GameStatus.Player1Turn);
            }
        } else if (action.getTypeOfAction() == TypeOfAction.Surrender) {
            if (player.getPlayerStatus() == PlayerStatus.Player1) {
                gameboard.setGameStatus(GameStatus.Player1Surrender);
            } else {
                gameboard.setGameStatus(GameStatus.Player2Surrender);
            }
        }
        gameboard = gameboardRepository.save(gameboard);

        Optional<GodCard> godCard = godCardRepository.findById(player.getGodCard().getId());

        if (godCard.isPresent()) {
            GodCard godCard1 = godCard.get();
            List<Action> turn = godCard1.getLastTurn();

            // if a new turn starts, empty list
            if (turn == null || (turn.size() > 0 && turn.get(turn.size() - 1).getTypeOfAction() == TypeOfAction.EndTurn)) {
                turn = new ArrayList<>();
            }

            turn.add(action);
            godCard1.setLastTurn(turn);
            godCardRepository.save(godCard1);
        }


        // check winning condition
        if (player.getGodCard().winningConditionMet(action) && !(action.getTypeOfAction() == TypeOfAction.Surrender)) {
            if (player.getPlayerStatus() == PlayerStatus.Player1) {
                gameboard.setGameStatus(GameStatus.Player1Won);
            } else {
                gameboard.setGameStatus(GameStatus.Player2Won);
            }
        }

        List<Field> fieldList;
        fieldList = fieldRepository.findAllByGameId(gameId);
        gameboard.setFields(fieldList);




        gameboardRepository.save(gameboard);

        return gameboard;
    }

    public void delete(Long playerId) {
        Gameboard gameboard = validationService.getGameboardByPlayerId(playerId);
        Player player = playerRepository.findPlayerById(playerId);
        GodCard godCard = godCardRepository.findGodCardById(player.getGodCard().getId());
        if (godCard.getLastTurn() != null) {
            List<Action> actions = godCard.getLastTurn();
            for (Action action: actions) {
                actionRepository.delete(action);
            }
        }

        if (gameboard.getPlayers().size() < 2) {
            List<Field> fields = gameboard.getFields();
            for (Field field: fields) {
                fieldRepository.delete(field);
            }
            playerRepository.delete(player);
            godCardRepository.delete(godCard);
            gameboardRepository.delete(gameboard);
        } else {
            List<Player> players = gameboard.getPlayers();
            players.remove(player);
            gameboard.setPlayers(players);
            List<GodCard> godCards = gameboard.getGodCards();
            godCards.remove(godCard);
            gameboard.setGodCards(godCards);
            gameboard.setGameStatus(GameStatus.PlayerRemoved);
            gameboardRepository.save(gameboard);
            playerRepository.delete(player);
            godCardRepository.delete(godCard);
        }
    }

    public Gameboard getCurrentGameboard(Long playerId) {
        return this.validationService.getGameboardByPlayerId(playerId);
    }

    private Field getFieldFromRepository(Field field, Long gameId) {
        if (field == null) {
            return null;
        }
        return fieldRepository.findByXCoordinateAndYCoordinateAndGameId(
                field.getxCoordinate(),
                field.getyCoordinate(),
                gameId);
    }
}
