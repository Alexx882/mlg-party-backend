package mlg.party.leaderboard.score;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/score")
public class ScoreController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}
