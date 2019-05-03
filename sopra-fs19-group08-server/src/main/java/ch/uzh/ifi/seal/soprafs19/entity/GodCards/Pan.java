package ch.uzh.ifi.seal.soprafs19.entity.GodCards;

import ch.uzh.ifi.seal.soprafs19.entity.Actions.Action;
import ch.uzh.ifi.seal.soprafs19.entity.Actions.TypeOfAction;
import ch.uzh.ifi.seal.soprafs19.entity.Field;

import javax.persistence.Entity;

@Entity
public class Pan extends GodCard {
    public Pan() {
        this.setName("Pan");
    }


    @Override
    public Boolean winningConditionMet(Action performedAction) {
        Field toField = performedAction.getToField();
        if (performedAction.getTypeOfAction() == TypeOfAction.Move && toField.getBuildingLevel() == 3) {
            return true;
        }
        if (getLastTurn().size() > 0) {
            Action lastAction = getLastTurn().get(getLastTurn().size() - 1);
            if (lastAction.getTypeOfAction() == TypeOfAction.Move &&
                    lastAction.getFromField().getBuildingLevel() - lastAction.getToField().getBuildingLevel() >= 2) {
                return true;
            }
        }

        // TODO: can also win if other cant move
        return false;
    }
}
