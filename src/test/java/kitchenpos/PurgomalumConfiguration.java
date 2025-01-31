package kitchenpos;

import kitchenpos.infra.PurgomalumClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class PurgomalumConfiguration {

    @Bean
    @Primary
    public PurgomalumClient mockPurgomalumClient() {
        return Mockito.mock(PurgomalumClient.class);
    }

}
