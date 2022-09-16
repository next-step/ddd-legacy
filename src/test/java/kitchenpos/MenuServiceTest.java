package kitchenpos;

import kitchenpos.application.MenuService;
import kitchenpos.domain.*;
import kitchenpos.factory.MenuGroupFactory;
import kitchenpos.factory.MenuProductFactory;
import kitchenpos.factory.ProductFactory;
import kitchenpos.infra.ProfanityClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private ProfanityClient profanityClient;

    @InjectMocks
    private MenuService menuService;

    @BeforeEach
    public void setUp() {
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
        profanityClient = new FakeProfanityClient();
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityClient);
    }

    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    public void create() {
        MenuGroup menuGroup = MenuGroupFactory.getDefaultMenuGroup();
        MenuGroup createMenuGroup = menuGroupRepository.save(menuGroup);

        Product 황금올리브 = productRepository.save(ProductFactory.of("황금올리브", 20000L));
        Product 호가든 = productRepository.save(ProductFactory.of("호가든", 5000L));

        List<MenuProduct> menuProducts = List.of(MenuProductFactory.of(황금올리브), MenuProductFactory.of(호가든));

        Menu request = new Menu();
        request.setMenuGroupId(createMenuGroup.getId());
        request.setName("치맥세트");
        request.setPrice(BigDecimal.valueOf(22000L));
        request.setMenuProducts(menuProducts);

        final Menu actual = menuService.create(request);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("치맥세트");
        assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(22000L));
        assertThat(actual.isDisplayed()).isEqualTo(false);
        assertThat(actual.getMenuProducts()).hasSize(2);
    }
}
