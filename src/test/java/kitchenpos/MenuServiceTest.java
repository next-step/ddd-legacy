package kitchenpos;

import kitchenpos.application.MenuService;
import kitchenpos.domain.*;
import kitchenpos.infra.ProfanityClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private ProfanityClient profanityClient;

    @InjectMocks
    private MenuService menuService;

    @BeforeEach
    public void setUp(){
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
        profanityClient = new FakeProfanityClient();
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityClient);
    }
}
