package kitchenpos.integration;

import kitchenpos.application.MenuGroupService;
import kitchenpos.application.ProductService;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.domain.ProfanityChecker;
import kitchenpos.integration.mock.FakeProfanityChecker;
import kitchenpos.integration.mock.MemoryMenuGroupRepository;
import kitchenpos.integration.mock.MemoryMenuRepository;
import kitchenpos.integration.mock.MemoryProductRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    MenuGroupService menuGroupService() {
        return new MenuGroupService(menuGroupRepository());
    }

    @Bean
    ProductService productService() {
        return new ProductService(
                productRepository(),
                menuRepository(),
                profanityChecker()
        );
    }

    @Bean
    MenuGroupRepository menuGroupRepository() {
        return new MemoryMenuGroupRepository();
    }

    @Bean
    MenuRepository menuRepository() {
        return new MemoryMenuRepository();
    }

    @Bean
    ProductRepository productRepository() {
        return new MemoryProductRepository();
    }

    @Bean
    ProfanityChecker profanityChecker() {
        return new FakeProfanityChecker();
    }
}
