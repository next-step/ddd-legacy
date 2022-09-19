package kitchenpos.configuration;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import kitchenpos.infra.PurgomalumClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@ActiveProfiles("test")
public class PurgomalumClientConfiguration {

	@Bean
	@Primary
	public PurgomalumClient testPurgomalumClient() {
		PurgomalumClient purgomalumClient = mock(PurgomalumClient.class);

		given(purgomalumClient.containsProfanity(any())).willReturn(false);

		return purgomalumClient;
	}
}
