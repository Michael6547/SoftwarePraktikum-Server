package ch.uzh.ifi.seal.soprafs19.entity.GodCards;

import ch.uzh.ifi.seal.soprafs19.entity.Actions.Action;
import ch.uzh.ifi.seal.soprafs19.entity.Actions.TypeOfAction;
import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.ValidMoves;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Hermes extends GodCard {

    public Hermes() {
        this.setName("Hermes");
    }

    @Override
    public ValidMoves getValidMoves(Field fromField, Field toField, Integer enemyPushable) {
        ValidMoves validMoves = new ValidMoves();

        List<Action> validActions = new ArrayList<>();

        Boolean movedUp = false;
        // case first action of turn
        if (getLastTurn() == null || getLastTurn().size() == 0 ||
                (getLastTurn().size() > 0 && getLastTurn().get(getLastTurn().size() - 1).getTypeOfAction() == TypeOfAction.EndTurn)) {
            if (toField.getBuildingLevel() - fromField.getBuildingLevel() == 0) {
                validActions.add(createAction(TypeOfAction.Move, fromField, toField));
            } else {
                if (isNeighbouringField(fromField.getxCoordinate(), fromField.getyCoordinate(),
                        toField.getxCoordinate(), toField.getyCoordinate())
                        && toField.getBuildingLevel() - fromField.getBuildingLevel() <= 1
                ) {
                    // can either move or build
                    validActions.add(createAction(TypeOfAction.Move, fromField, toField));
                    validActions.add(createAction(TypeOfAction.Build, fromField, toField));
                }
            }
        }

        // second move
        else if (getLastTurn().get(getLastTurn().size() - 1).getTypeOfAction() == TypeOfAction.Move) {
            Action performedAction = getLastTurn().get(getLastTurn().size() - 1);
            if (performedAction.getToField().getBuildingLevel() - performedAction.getFromField().getBuildingLevel() <= 0
                    && toField.getBuildingLevel() - fromField.getBuildingLevel() <= 0
            ) {
                validActions.add(createAction(TypeOfAction.Move, fromField, toField));
            }
        }

        // build
        else if (getLastTurn().size() > 1 &&
                getLastTurn().get(getLastTurn().size() - 1).getTypeOfAction() == TypeOfAction.Move
        ) {
            validActions.add(createAction(TypeOfAction.Build, fromField, toField));
        }

        return validMoves;

    }


}
