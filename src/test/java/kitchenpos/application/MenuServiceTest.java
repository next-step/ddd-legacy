package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MenuServiceTest {

    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final PurgomalumClient purgomalumClient = new FakePurgomalumClient();
    private final ProductRepository productRepository = new InMemoryProductRepository();
    private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();


    @InjectMocks
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @DisplayName("메뉴 가격이 0보다 작으면 예외가 발생한다.")
    @ValueSource(longs = {-1000L})
    @ParameterizedTest
    void create(final long price) {
        //given
        final Menu request = createMenuRequest(price);
        // when, then
        assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴의 메뉴 그룹 ID가 메뉴 그룹 저장소에 없으면 예외가 발생한다.")
    @Test
    void create2() {
        //given
        final MenuGroup requestMenuGroup = createMenuGroupRequest("한마리메뉴");
        MenuGroup actualMenuGroup = menuGroupRepository.save(requestMenuGroup);

        final MenuGroup request2MenuGroup = createMenuGroupRequest("두마리메뉴");

        final Menu request = createMenuRequest(20_000L, request2MenuGroup.getId());


        // when, then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> menuService.create(request));

    }


    private Menu createMenuRequest(final long price, UUID menuGroupId) {
        final Menu request = new Menu();
        request.setName("후라이드");
        request.setPrice(BigDecimal.valueOf(price));
        request.setMenuGroupId(menuGroupId);
        return request;
    }

    private static MenuGroup createMenuGroupRequest() {
        final MenuGroup requestMenuGroup = new MenuGroup();
        requestMenuGroup.setId(UUID.randomUUID());
        requestMenuGroup.setName("한마리메뉴");
        return createMenuGroupRequest(requestMenuGroup.getName());
    }

    private static MenuGroup createMenuGroupRequest(final String name) {
        final MenuGroup requestMenuGroup = new MenuGroup();
        requestMenuGroup.setId(UUID.randomUUID());
        requestMenuGroup.setName(name);
        return requestMenuGroup;
    }

    private static Menu createMenuRequest() {
        final Menu request = new Menu();
        request.setName("후라이드");
        request.setPrice(BigDecimal.valueOf(16_000L));
        return createMenuRequest(request.getName(), request.getPrice());
    }

    private static Menu createMenuRequest(final long price) {
        final Menu request = new Menu();
        request.setName("후라이드");
        request.setPrice(BigDecimal.valueOf(price));
        return createMenuRequest(request.getName(), request.getPrice());
    }

    private static Menu createMenuRequest(final List<Product> products) {
        final Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setName("후라이드");
        request.setPrice(BigDecimal.valueOf(16_000L));

        return createMenuRequest(products, request.getId());
    }


    private static Menu createMenuRequest(final String name, final BigDecimal price) {
        final Menu request = new Menu();
        request.setName(name);
        request.setPrice(price);
        request.setMenuGroupId(UUID.randomUUID());

        return createMenuRequest(request.getName(), request.getPrice(), request.getMenuGroupId());

    }

    private static Menu createMenuRequest(final String name, final BigDecimal price, final UUID menuGroupId) {
        final Menu request = new Menu();

        request.setName(name);
        request.setPrice(price);
        request.setMenuGroupId(menuGroupId);
        request.setDisplayed(true);
        request.setMenuGroup(createMenuGroupRequest());
        Product productRequest = createProductRequest();

        request.setMenuProducts(
                Stream.of(productRequest).map(product -> {
                    final MenuProduct menuProduct = new MenuProduct();
                    menuProduct.setProduct(productRequest);
                    menuProduct.setQuantity(1L);
                    return menuProduct;
                }).toList()
        );
        request.setMenuGroupId(menuGroupId);

        return createMenuRequest(
                request.getName(), request.getPrice(), request.isDisplayed(),
                request.getMenuProducts(), request.getMenuGroupId(), request.getId(),
                request.getMenuGroup());

    }

    private static Menu createMenuRequest(final List<Product> products, final UUID menuGroupId) {
        final Menu request = new Menu();

        request.setId(UUID.randomUUID());
        request.setName("후라이드");
        request.setPrice(BigDecimal.valueOf(16_000L));
        request.setDisplayed(true);
        request.setMenuGroup(createMenuGroupRequest());
        request.setMenuProducts(
                products.stream().map(product -> {
                    final MenuProduct menuProduct = new MenuProduct();
                    menuProduct.setProduct(product);
                    menuProduct.setQuantity(1L);
                    return menuProduct;
                }).toList()
        );
        request.setMenuGroupId(menuGroupId);

        return createMenuRequest(
                request.getName(), request.getPrice(), request.isDisplayed(),
                request.getMenuProducts(), request.getMenuGroupId(), request.getId(),
                request.getMenuGroup());
    }

    private static Menu createMenuRequest(
            final String name, final BigDecimal price, final boolean displayed
            , final List<MenuProduct> menuProducts, final UUID menuGroupId,
            final UUID id, final MenuGroup menuGroup) {
        final Menu request = new Menu();
        request.setName(name);
        request.setPrice(price);
        request.setDisplayed(displayed);
        request.setMenuGroup(menuGroup);
        request.setMenuGroupId(menuGroupId);
        request.setId(id);
        request.setMenuProducts(menuProducts);

        return request;
    }

    private static Product createProductRequest() {
        final Product request = new Product();
        request.setName("후라이드");
        request.setPrice(BigDecimal.valueOf(16_000L));
        return request;
    }


}