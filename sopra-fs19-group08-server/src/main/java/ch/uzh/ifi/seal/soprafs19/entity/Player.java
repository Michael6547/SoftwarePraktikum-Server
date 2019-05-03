package ch.uzh.ifi.seal.soprafs19.entity;

import ch.uzh.ifi.seal.soprafs19.entity.GodCards.GodCard;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;

@Entity
public class
Player implements Serializable {
	

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable = false, unique = true) 
	private String playerName;
	
	@Column(nullable = false, unique = true) 
	private String token;

	@ManyToOne
	@JoinColumn(name = "player_id")
	private GodCard godCard;

	@Column
	@Enumerated(value = EnumType.STRING)
	private PlayerStatus playerStatus;

	private Long gameId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public GodCard getGodCard() {
		return godCard;
	}

	public void setGodCard(GodCard godCard) {
		this.godCard = godCard;
	}

	public Long getGameId() {
		return gameId;
	}

	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}

	public PlayerStatus getPlayerStatus() {
		return playerStatus;
	}

	public void setPlayerStatus(PlayerStatus playerStatus) { this.playerStatus = playerStatus; }

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Player)) {
			return false;
		}
		Player player = (Player) o;
		return this.getId().equals(player.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
