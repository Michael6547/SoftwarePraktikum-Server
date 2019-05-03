package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.Actions.Action;
import ch.uzh.ifi.seal.soprafs19.entity.Gameboard;
import ch.uzh.ifi.seal.soprafs19.entity.RequestMoves;
import ch.uzh.ifi.seal.soprafs19.entity.ValidMoves;
import ch.uzh.ifi.seal.soprafs19.service.GameboardService;
import org.springframework.web.bind.annotation.*;

@RestController
public class GameboardController {

    private final GameboardService gameboardService;

    GameboardController(GameboardService gameboardService) {
        this.gameboardService = gameboardService;
    }

    @GetMapping("/gameboard/{playerId}")
    Gameboard getCurrentGameboard(@PathVariable Long playerId) {
        return this.gameboardService.getCurrentGameboard(playerId);
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/gameboard/{playerId}/validMoves")
    ValidMoves getValidMoves(@PathVariable Long playerId, @RequestBody RequestMoves requestMoves) {
        return this.gameboardService.getValidMoves(playerId, requestMoves, 0);
    }

    @PostMapping("/gameboard/{playerId}/action")
    Gameboard performAction(@PathVariable Long playerId, @RequestBody Action action) {
        return this.gameboardService.performAction(playerId, action);
    }

    @DeleteMapping("gameboard/{playerId}")
    void delete(@PathVariable long playerId) {
        this.gameboardService.delete(playerId);
    }
}
