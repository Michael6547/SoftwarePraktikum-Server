package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Gameboard;
import ch.uzh.ifi.seal.soprafs19.repository.GameboardRepository;
import ch.uzh.ifi.seal.soprafs19.service.GameboardService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FastForwardController {

    private final GameboardRepository gameboardRepository;
    private final GameboardService gameboardService;

    FastForwardController(GameboardService gameboardService, @Qualifier("gameboardRepository") GameboardRepository gameboardRepository) {
        this.gameboardService = gameboardService;
        this.gameboardRepository = gameboardRepository;
    }

    @PostMapping("/fastforward/{playerId}")
    public void fastForward(@PathVariable long playerId){
        Gameboard gb = this.gameboardService.getCurrentGameboard(playerId);
        List<Field> fields = gb.getFields();
        fields.get(0).setBuildingLevel(2);
        fields.get(1).setBuildingLevel(3);
        fields.get(2).setBuildingLevel(2);
        fields.get(3).setBuildingLevel(2);
        fields.get(4).setBuildingLevel(3);
        fields.get(5).setBuildingLevel(2);
        fields.get(6).setBuildingLevel(2);
        fields.get(7).setBuildingLevel(3);
        fields.get(8).setBuildingLevel(2);
        fields.get(9).setBuildingLevel(3);
        fields.get(10).setBuildingLevel(2);
        fields.get(11).setBuildingLevel(2);
        fields.get(12).setBuildingLevel(3);
        fields.get(13).setBuildingLevel(2);
        fields.get(14).setBuildingLevel(2);
        fields.get(15).setBuildingLevel(3);
        fields.get(16).setBuildingLevel(2);
        fields.get(17).setBuildingLevel(3);
        fields.get(18).setBuildingLevel(2);
        fields.get(19).setBuildingLevel(2);
        fields.get(20).setBuildingLevel(3);
        fields.get(21).setBuildingLevel(2);
        fields.get(22).setBuildingLevel(2);
        fields.get(23).setBuildingLevel(3);
        fields.get(24).setBuildingLevel(2);

        gb.setFields(fields);

        this.gameboardRepository.save(gb);

    }

}
