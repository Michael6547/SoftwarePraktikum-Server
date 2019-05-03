package ch.uzh.ifi.seal.soprafs19.entity.GodCards;

import ch.uzh.ifi.seal.soprafs19.entity.Actions.Action;
import ch.uzh.ifi.seal.soprafs19.entity.Actions.TypeOfAction;
import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.ValidMoves;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance
public class GodCard implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(fetch = FetchType.EAGER)
    @OrderColumn(name = "ind")
    @JoinColumn(name="godcard_id")
    private List<Action> lastTurn;

    public GodCard() {
        this.setName("Basic");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Action> getLastTurn() {
        return lastTurn;
    }

    public void setLastTurn(List<Action> lastTurn) {
        this.lastTurn = lastTurn;
    }

    public ValidMoves getValidMoves(Field fromField, Field toField, Integer enemyPushable) {
        ValidMoves validMoves = new ValidMoves();

        List<Action> validActions = new ArrayList<>();




        if (lastTurn != null && lastTurn.size() == 2 &&
                lastTurn.get(0).getTypeOfAction() == TypeOfAction.Move &&
                lastTurn.get(1).getTypeOfAction() == TypeOfAction.Build) {
            validActions.add(createAction(TypeOfAction.EndTurn, null, null));
            validMoves.setValidActions(validActions);
            return validMoves;
        } else if (!isNeighbouringField(fromField.getxCoordinate(), fromField.getyCoordinate(),
                toField.getxCoordinate(), toField.getyCoordinate())) {
            validMoves.setValidActions(validActions);
            return validMoves;
        }

        // case where player has just started new turn
        if ((lastTurn == null || lastTurn.size() == 0) && toField.getBuildingLevel() != null
                && toField.getBuildingLevel() - fromField.getBuildingLevel() <= 1 ) {
            if (toField.getPlayerId() == null) {
                validActions.add(createAction(TypeOfAction.Move, fromField, toField));
            }

        // case where player has already moved
        } else if (lastTurn != null && lastTurn.size() == 1
                && lastTurn.get(0).getTypeOfAction() == TypeOfAction.Move
                && toField.getBuildingLevel() != null
                && !toField.getHasDome()) {
            Action build = new Action();
            build.setTypeOfAction(TypeOfAction.Build);
            build.setToField(toField);
            if (toField.getBuildingLevel() < 3) {
                build.setBuildDome(false);
            } else {
                build.setBuildDome(true);
            }
            validActions.add(build);

        // case where turn is finished, can only end turn
        }

        validMoves.setValidActions(validActions);
        return validMoves;
    }

    public Boolean winningConditionMet(Action performedAction) {
        Field toField = performedAction.getToField();
        if (performedAction.getTypeOfAction() == TypeOfAction.Move && toField.getBuildingLevel() == 3) {
            return true;
        }
        // TODO: can also win if other cant move
        return false;
    }

    public boolean isNeighbouringField(Integer xOrigin, Integer yOrigin, Integer xTarget, Integer yTarget) {
        if (Math.abs(xOrigin - xTarget) <= 1 && Math.abs(yOrigin - yTarget) <= 1) {
            return true;
        }
        return false;

    }

    public Action createAction(TypeOfAction typeOfAction, Field fromField, Field toField) {
        Action action = new Action();
        action.setTypeOfAction(typeOfAction);
        action.setFromField(fromField);
        action.setToField(toField);

        return action;
    }
}
