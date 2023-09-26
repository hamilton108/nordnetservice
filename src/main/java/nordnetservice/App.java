package nordnetservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
		  "nordnetservice.adapter"
		, "nordnetservice.api"
		, "nordnetservice.config"
		, "nordnetservice.domain.core"
		, "vega.financial.calculator"
})
public class App {

	public App() {
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	/*
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
	 */

	/*
	@Override
	public void run(String... args) throws Exception {
		SpringApplication.run(App.class, args);
	}

	 */
}
