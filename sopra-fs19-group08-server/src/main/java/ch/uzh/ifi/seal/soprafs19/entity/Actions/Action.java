package ch.uzh.ifi.seal.soprafs19.entity.Actions;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Player;

import javax.persistence.*;
import java.io.Serializable;


@Entity
public class Action implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    private TypeOfAction typeOfAction;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="field_id", referencedColumnName="id", insertable=false, updatable=false)
    private Field fromField;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="field_id", referencedColumnName="id", insertable=false, updatable=false)
    private Field toField;

    private Boolean buildDome; // build dome on top of building that is on field

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TypeOfAction getTypeOfAction() {
        return typeOfAction;
    }

    public void setTypeOfAction(TypeOfAction typeOfAction) {
        this.typeOfAction = typeOfAction;
    }

    public Field getFromField() {
        return fromField;
    }

    public void setFromField(Field fromField) {
        this.fromField = fromField;
    }

    public Field getToField() {
        return toField;
    }

    public void setToField(Field toField) {
        this.toField = toField;
    }

    public Boolean getBuildDome() {
        return buildDome;
    }

    public void setBuildDome(Boolean buildDome) {
        this.buildDome = buildDome;
    }
}
