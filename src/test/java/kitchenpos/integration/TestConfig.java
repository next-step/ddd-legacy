package kitchenpos.integration;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.integration.mock.FakeMenuGroupRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    MenuGroupService menuGroupService() {
        return new MenuGroupService(menuGroupRepository());
    }

    @Bean
    MenuGroupRepository menuGroupRepository() {
        return new FakeMenuGroupRepository();
    }
}
