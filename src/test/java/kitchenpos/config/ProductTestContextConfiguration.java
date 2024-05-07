package kitchenpos.config;

import kitchenpos.application.ProductService;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fakeClient.FakePurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ProductTestContextConfiguration {
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private ProductRepository productRepository;

    @Bean
    public PurgomalumClient purgomalumClient(){
        return new FakePurgomalumClient(new RestTemplateBuilder());
    };

    @Bean
    public ProductService productService(){
        return new ProductService(productRepository
                , menuRepository
                , purgomalumClient());
    }
}
