package ch.uzh.ifi.seal.soprafs19.entity;

import ch.uzh.ifi.seal.soprafs19.entity.Actions.Action;

import java.util.Collection;
import java.util.List;

public class ValidMoves {
    private List<Action> validActions;

    public List<Action> getValidActions() {
        return validActions;
    }

    public void setValidActions(List<Action> validActions) {
        this.validActions = validActions;
    }
}
