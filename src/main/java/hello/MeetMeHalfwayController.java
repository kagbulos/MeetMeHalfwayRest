package hello;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeetMeHalfwayController {

    private static final String template = "you rule, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/mmh")
    public MeetMeHalfway greeting(@RequestParam(value="location1", defaultValue="Mountain View, California") String location1, 
    						@RequestParam(value="location2", defaultValue="Los Angeles, California") String location2,
    						@RequestParam(value="type", defaultValue="sushi") String type,
    						@RequestParam(value="limit", defaultValue="5") String limit) {
        return new MeetMeHalfway(counter.incrementAndGet(),
                            location1, location2, type, limit);
    }
}
