package ch.uzh.ifi.seal.soprafs19.controller;
import ch.uzh.ifi.seal.soprafs19.entity.LoginRequest;
import ch.uzh.ifi.seal.soprafs19.entity.LoginResponse;
import org.springframework.http.HttpStatus;

import ch.uzh.ifi.seal.soprafs19.service.PlayerService;
import org.springframework.web.bind.annotation.*;

@RestController
public class PlayerController {

    private final PlayerService service;

    PlayerController(PlayerService service) {
        this.service = service;
    }

    @PostMapping("/players/login")
    @ResponseStatus(HttpStatus.CREATED)
    LoginResponse loginPlayer(@RequestBody LoginRequest loginRequest) {
        return this.service.createPlayer(loginRequest);
    }

}
