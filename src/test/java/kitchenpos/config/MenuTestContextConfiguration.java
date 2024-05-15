package kitchenpos.config;

import kitchenpos.application.MenuService;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fakeClient.FakePurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MenuTestContextConfiguration {
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private ProductRepository productRepository;

    private FakePurgomalumClient profanityClient = new FakePurgomalumClient();

    @Bean
    public MenuService menuService(){
        return new MenuService(menuRepository
                , menuGroupRepository
                , productRepository
                , profanityClient);
    }
}
