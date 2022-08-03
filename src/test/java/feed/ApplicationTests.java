package feed;

import java.util.function.Supplier;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ActiveProfiles("test")
@FunctionalSpringBootTest
class ApplicationTests extends BDDAssertions {

  @Autowired private FunctionCatalog functions;

  @Test
  void shouldPing() {

    // given
    var expected = "pong";

    // when
    Supplier<Flux<String>> ping = functions.lookup("ping");

    // then
    StepVerifier.create(ping.get()).expectNext(expected).verifyComplete();
  }

}
