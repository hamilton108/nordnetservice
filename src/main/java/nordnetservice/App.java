package nordnetservice;

import nordnetservice.adapter.CritterAdapter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class App {

	private final RedisTemplate<String,Object> redisTemplate;

	private final CritterAdapter critterAdapter;

	public App(RedisTemplate<String,Object> redisTemplate,
			   CritterAdapter critterAdapter) {
		this.redisTemplate = redisTemplate;
		this.critterAdapter = critterAdapter;
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@GetMapping("/greeting")
	public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		//return new Greeting(counter.incrementAndGet(), String.format(template, name));
		var tmp = redisTemplate.opsForSet().members("expiry");
		StringBuilder result = new StringBuilder("Here we go:");
		for (var s : tmp.toArray()) {
			result.append(s);
		}
		var puch = critterAdapter.fetchCritters(11);
		System.out.println(puch);
		return result.toString();
	}

	/*
	@Override
	public void run(String... args) throws Exception {
		SpringApplication.run(App.class, args);
	}

	 */
}
