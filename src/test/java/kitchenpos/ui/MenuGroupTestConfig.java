package kitchenpos.ui;

import kitchenpos.application.MenuGroupService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MenuGroupTestConfig {

    @Bean
    public MenuGroupService menuGroupService() {
        return new FakeMenuGroupService();
    }
}
