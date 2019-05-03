package ch.uzh.ifi.seal.soprafs19.entity;

import javax.persistence.*;

@Entity
public class Field {

    @Id
    @GeneratedValue()
    private Long id;
    @JoinColumn(name = "gameboard_id")
    private Long gameId;
    private Integer xCoordinate;
    private Integer yCoordinate;
  //  @NotNull
  //  @Range(min = 0, max = 3);
    private Integer buildingLevel;
    private Boolean hasDome = false;

    private Long playerId; // contains playerId if playerId has a worker on that field


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Integer getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(Integer xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public Integer getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(Integer yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public Integer getBuildingLevel() {
        return buildingLevel;
    }

    public void setBuildingLevel(Integer buildingLevel) {
        this.buildingLevel = buildingLevel;
    }

    public Boolean getHasDome() {
        return hasDome;
    }

    public void setHasDome(Boolean hasDome) {
        this.hasDome = hasDome;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }
}
