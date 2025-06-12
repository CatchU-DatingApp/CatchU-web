package catchu_datingapp.CatchU_Web.controller;

import catchu_datingapp.CatchU_Web.model.Match;
import catchu_datingapp.CatchU_Web.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @GetMapping("/user/{userId}")
    public List<Match> getMatchesForUser(@PathVariable String userId) throws ExecutionException, InterruptedException {
        return matchService.getMatchesForUser(userId);
    }

    @GetMapping("/{id}")
    public Match getMatchById(@PathVariable String id) throws ExecutionException, InterruptedException {
        return matchService.getMatchById(id);
    }

    @PostMapping
    public String createOrUpdateMatch(@RequestBody Match match) throws ExecutionException, InterruptedException {
        return matchService.createOrUpdateMatch(match);
    }

    @DeleteMapping("/{id}")
    public void deleteMatch(@PathVariable String id) throws ExecutionException, InterruptedException {
        matchService.deleteMatch(id);
    }
}
