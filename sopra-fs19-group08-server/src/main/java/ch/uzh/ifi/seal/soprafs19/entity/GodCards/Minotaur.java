package ch.uzh.ifi.seal.soprafs19.entity.GodCards;

import ch.uzh.ifi.seal.soprafs19.entity.Actions.Action;
import ch.uzh.ifi.seal.soprafs19.entity.Actions.TypeOfAction;
import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.ValidMoves;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Minotaur extends GodCard {

    public Minotaur() {
        this.setName("Minotaur");
    }

    public ValidMoves getValidMoves(Field fromField, Field toField, Integer enemyPushable) {
        ValidMoves validMoves = new ValidMoves();

        List<Action> validActions = new ArrayList<>();

        if (getLastTurn() == null || getLastTurn().size() == 0 || (getLastTurn().size() > 0 && getLastTurn().get(getLastTurn().size() - 1).getTypeOfAction() == TypeOfAction.EndTurn)){
            if (toField.getPlayerId() != fromField.getPlayerId() && toField.getPlayerId() != null){
                if (enemyPushable == 1){
                    Action action = new Action();
                    action.setTypeOfAction(TypeOfAction.Move);
                    action.setToField(toField);
                    action.setFromField(fromField);
                    validActions.add(action);
                }
                //check if the enemy player can be pushed there
            }
            else{
                Action action = new Action();
                action.setTypeOfAction(TypeOfAction.Move);
                action.setToField(toField);
                action.setFromField(fromField);
                validActions.add(action);
            }

        }


        else if (getLastTurn().size() >= 1 && getLastTurn().get(getLastTurn().size() - 1).getTypeOfAction() == TypeOfAction.Move){
            Action action = new Action();
            action.setTypeOfAction(TypeOfAction.Build);
            action.setToField(toField);
            validActions.add(action);
        }

        validMoves.setValidActions(validActions);
        return validMoves;

    }
}
