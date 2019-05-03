package ch.uzh.ifi.seal.soprafs19.entity.GodCards;

import ch.uzh.ifi.seal.soprafs19.entity.Actions.Action;
import ch.uzh.ifi.seal.soprafs19.entity.Actions.TypeOfAction;
import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.ValidMoves;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Prometheus extends GodCard {

    public Prometheus() {
        this.setName("Prometheus");
    }


    public ValidMoves getValidMoves(Field fromField, Field toField, Integer enemyPushable) {
        ValidMoves validMoves = new ValidMoves();

        List<Action> validActions = new ArrayList<>();

        if (getLastTurn() == null || getLastTurn().size() == 0 || (getLastTurn().size() > 0 && getLastTurn().get(getLastTurn().size() - 1).getTypeOfAction() == TypeOfAction.EndTurn)){
            if (toField.getPlayerId() == null) {
                Action action = new Action();
                action.setTypeOfAction(TypeOfAction.Build);
                action.setToField(toField);
                action.setFromField(fromField);
                validActions.add(action);

                if (toField.getBuildingLevel() - fromField.getBuildingLevel() <=1) {
                    Action action2 = new Action();
                    action2.setTypeOfAction(TypeOfAction.Move);
                    action2.setToField(toField);
                    action2.setFromField(fromField);
                    validActions.add(action2);
                }
            }
        }

        else {
            if (getLastTurn().size() != 0 && (getLastTurn().get(getLastTurn().size() -1).getTypeOfAction() == TypeOfAction.Build) && (isNeighbouringField(fromField.getxCoordinate(), fromField.getyCoordinate(),
                    toField.getxCoordinate(), toField.getyCoordinate())
                    && toField.getBuildingLevel() - fromField.getBuildingLevel() == 0)){     //check if the last turn was build, if it was build the player cannot move up or down
                if (getLastTurn().size() >= 3) {
                    if (((getLastTurn().get(getLastTurn().size() - 3).getTypeOfAction() == TypeOfAction.EndTurn))) {
                        Action action = new Action();
                        action.setTypeOfAction(TypeOfAction.Move);
                        action.setFromField(fromField);
                        action.setToField(toField);
                        validActions.add(action);
                    }
                }
                else {
                    Action action = new Action();
                    action.setTypeOfAction(TypeOfAction.Move);
                    action.setFromField(fromField);
                    action.setToField(toField);
                    validActions.add(action);
                }
            }

            else if (getLastTurn().size() >= 1 && getLastTurn().get(getLastTurn().size() - 1).getTypeOfAction() == TypeOfAction.Move){
                Action action = new Action();
                action.setTypeOfAction(TypeOfAction.Build);
                action.setToField(toField);
                validActions.add(action);
            }
        }

        validMoves.setValidActions(validActions);
        return validMoves;
    }
}
