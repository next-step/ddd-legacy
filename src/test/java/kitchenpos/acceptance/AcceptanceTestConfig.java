package kitchenpos.acceptance;

import kitchenpos.fixture.fake.FakeProfanityClient;
import kitchenpos.domain.ProfanityClient;
import kitchenpos.utils.DatabaseCleanUp;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.persistence.EntityManager;

@TestConfiguration
public class AcceptanceTestConfig {

    private final EntityManager entityManager;

    public AcceptanceTestConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Bean
    @Primary
    public ProfanityClient fakeProfanityClient() {
        return new FakeProfanityClient();
    }

    @Bean
    public DatabaseCleanUp databaseCleanUp() {
        return new DatabaseCleanUp(entityManager);
    }
}
