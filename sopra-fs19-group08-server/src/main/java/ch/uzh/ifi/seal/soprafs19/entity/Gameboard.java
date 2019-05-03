package ch.uzh.ifi.seal.soprafs19.entity;

import ch.uzh.ifi.seal.soprafs19.entity.GodCards.GodCard;

import javax.persistence.*;
import java.util.List;

@Entity
public class Gameboard {
    @Id
    @GeneratedValue
    private long id;

    @Column
    @OneToMany(fetch = FetchType.EAGER)
    @OrderColumn(name = "ind")
    @JoinColumn(name="gameboard_id")
    private List<Field> fields;

    @Column
    @OneToMany(fetch = FetchType.EAGER)
    @OrderColumn(name = "ind")
    @JoinColumn(name="gameboard_id")
    private List<Player> players;

    @Column
    @Enumerated(value = EnumType.STRING)
    private GameStatus gameStatus;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="gameboard_id")
    private List<GodCard> godCards; // The two selected godcards that are available in the game

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public List<GodCard> getGodCards() {
        return godCards;
    }

    public void setGodCards(List<GodCard> godCards) {
        this.godCards = godCards;
    }
}
