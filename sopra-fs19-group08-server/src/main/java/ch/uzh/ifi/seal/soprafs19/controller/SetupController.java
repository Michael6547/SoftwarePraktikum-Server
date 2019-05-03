package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Gameboard;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.service.SetupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SetupController {

    private SetupService service;

    SetupController(SetupService service) {this.service = service;}

    @PostMapping("/setup/{playerId}/godCards")
    void setGodCards(@PathVariable Long playerId, @RequestBody List<String> godcards) {
        this.service.setGodCards(playerId, godcards);
    }

    @PostMapping("/setup/{playerId}/workers")
    void setWorker(@PathVariable Long playerId, @RequestBody Field field) {
        this.service.setWorker(playerId, field);
    }

    @GetMapping("/test")
    Iterable<Gameboard> returnGameboard() { return this.service.getAllGameboards();}

    @GetMapping("/testPlayers")
    Iterable<Player> returnPlayers() {return this.service.getAllPlayers();}
}
