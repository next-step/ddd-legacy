package kitchenpos.config;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroupRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MenuGroupTestContextConfiguration {
    @MockBean
    private MenuGroupRepository menuGroupRepository;

    @Bean
    public MenuGroupService menuGroupService(){
        return new MenuGroupService(menuGroupRepository);
    }
}
