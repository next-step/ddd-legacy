package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.NoSuchElementException;

import static kitchenpos.application.MenuFixture.createMenuRequest;
import static kitchenpos.application.MenuFixture.createMenuRequestExceptMenuProduct;
import static kitchenpos.application.MenuGroupFixture.createMenuGroupRequest;
import static kitchenpos.application.MenuProductFixture.createMenuProductRequest;
import static kitchenpos.application.ProductFixture.createProductRequest;
import static org.assertj.core.api.Assertions.*;

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
        final Menu request = createMenuRequest(20_000L, request2MenuGroup);

        // when, then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> menuService.create(request));

    }

    @DisplayName("추가할 메뉴의 메뉴상품이 없으면 예외가 발생한다.")
    @Test
    void create3() {
        // given
        final MenuGroup requestMenuGroup = createMenuGroupRequest("한마리메뉴");
        MenuGroup actualMenuGroup = menuGroupRepository.save(requestMenuGroup);

        final Product productRequest = createProductRequest("후라이드", 16_000L);

        final MenuProduct menuProduct = createMenuProductRequest(productRequest);

        final Menu request = createMenuRequestExceptMenuProduct(actualMenuGroup);
        final Menu actualMenu = menuRepository.save(request);


        //when, then
        assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴의 메뉴상품의 상품이 등록되어 있지 않다면 예외가 발생한다.")
    @Test
    void create4() {
        // given
        final MenuGroup requestMenuGroup = createMenuGroupRequest("한마리메뉴");
        MenuGroup actualMenuGroup = menuGroupRepository.save(requestMenuGroup);

        final Product productRequest = createProductRequest("후라이드", 16_000L);

        final MenuProduct menuProduct = createMenuProductRequest(productRequest);

        final Menu request = createMenuRequest(actualMenuGroup, menuProduct);
        final Menu actualMenu = menuRepository.save(request);
        final Product productRequest1 = createProductRequest("양념치킨");
        productRepository.save(productRequest1);

        // when, then
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> menuService.create(actualMenu));
    }


    @DisplayName("메뉴 이름에 비속어가 있으면 예외가 발생한다.")
    @Test
    void create5() {
        // given
        final MenuGroup requestMenuGroup = createMenuGroupRequest("한마리메뉴");
        final MenuGroup actualMenuGroup = menuGroupRepository.save(requestMenuGroup);

        final Product requestProduct = createProductRequest("후라이드", 16_000L);
        final Product actualProduct = productRepository.save(requestProduct);

        final MenuProduct menuProductRequest = createMenuProductRequest(actualProduct);

        final Menu actualMenu = createMenuRequest("비속어", actualMenuGroup, List.of(menuProductRequest));

        // when, then
        assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(actualMenu));

    }

    @DisplayName("메뉴를 등록한다.")
    @Test
    void create6() {
        // given
        final MenuGroup requestMenuGroup = createMenuGroupRequest("한마리메뉴");
        final MenuGroup actualMenuGroup = menuGroupRepository.save(requestMenuGroup);

        final Product requestProduct = createProductRequest("후라이드", 16_000L);
        final Product actualProduct = productRepository.save(requestProduct);

        final MenuProduct menuProductRequest = createMenuProductRequest(actualProduct);

        final Menu actualMenu = createMenuRequest(actualMenuGroup, List.of(menuProductRequest));

        // when
        menuService.create(actualMenu);

        // then
        assertThat(menuRepository.findAll()).hasSize(1);
    }

    @DisplayName("메뉴 가격을 변경한다.")
    @Test
    void changePrice(){
        // given
        final Product productRequest = createProductRequest("후라이드", 16_000L);
        final MenuProduct menuProductRequest = createMenuProductRequest(productRequest);
        final Menu menuRequest = createMenuRequest(16_000L, List.of(menuProductRequest));
        final Menu actualMenu = menuRepository.save(menuRequest);

        // when
        final Menu chagePriceMenuRequest = createMenuRequest(15_000L);
        menuService.changePrice(actualMenu.getId(), chagePriceMenuRequest);

        // then
        assertThat(menuRepository.findById(actualMenu.getId()).get().getPrice()).isEqualTo(chagePriceMenuRequest.getPrice());
    }

    @DisplayName("상품 가격보다 메뉴 가격을 더 높게 변경하면 예외가 발생한다.")
    @Test
    void changePrice2(){
        // given
        final Product productRequest = createProductRequest("후라이드", 16_000L);
        final MenuProduct menuProductRequest = createMenuProductRequest(productRequest);
        final Menu menuRequest = createMenuRequest(16_000L, List.of(menuProductRequest));
        final Menu actualMenu = menuRepository.save(menuRequest);

        // when, then
        final Menu chagePriceMenuRequest = createMenuRequest(20_000L);

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> menuService.changePrice(actualMenu.getId(), chagePriceMenuRequest));
    }

    @DisplayName("메뉴를 전시 상태로 변경한다.")
    @Test
    void display(){
        // given
        final Product productRequest = createProductRequest("후라이드", 16_000L);
        final MenuProduct menuProductRequest = createMenuProductRequest(productRequest);
        final Menu menuRequest = createMenuRequest(16_000L, List.of(menuProductRequest));
        final Menu actualMenu = menuRepository.save(menuRequest);

        // when
        menuService.display(actualMenu.getId());

        // then
        assertThat(menuRepository.findById(actualMenu.getId()).get().isDisplayed()).isTrue();
    }

    @DisplayName("메뉴를 비전시 상태로 변경한다.")
    @Test
    void hide(){
        // given
        final Product productRequest = createProductRequest("후라이드", 16_000L);
        final MenuProduct menuProductRequest = createMenuProductRequest(productRequest);
        final Menu menuRequest = createMenuRequest(16_000L, List.of(menuProductRequest));
        final Menu actualMenu = menuRepository.save(menuRequest);
        menuService.display(actualMenu.getId());

        // when
        menuService.hide(actualMenu.getId());

        // then
        assertThat(menuRepository.findById(actualMenu.getId()).get().isDisplayed()).isFalse();
    }
}