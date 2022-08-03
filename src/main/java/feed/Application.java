package feed;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class Application {

  public static void main(String... args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  Supplier<Flux<String>> ping() {
    return () -> Flux.just("pong");
  }

  @Bean
  Supplier<Flux<Long>> supplier() {
    return () -> Flux.interval(Duration.ofMillis(200));
  }

  @Bean
  Function<Flux<String>, Flux<String>> publisher() {
    return in -> in;
  }

}
