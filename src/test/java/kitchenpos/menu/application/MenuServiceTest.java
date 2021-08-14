package kitchenpos.menu.application;

import kitchenpos.application.MenuService;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static kitchenpos.menu.fixture.MenuFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@DisplayName("Menu 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    private MenuService menuService;

    private MenuGroup 기본_한마리_메뉴_그룹;
    private Product 기본_후라이드_상품;
    private Menu 기본_후라이드_치킨_메뉴;
    private UUID id;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
        기본_한마리_메뉴_그룹 = 한마리_메뉴_그룹();
        기본_후라이드_상품 = 후라이드_상품();
        기본_후라이드_치킨_메뉴 = 후라이드_치킨_메뉴();
        id = UUID.randomUUID();
    }

    @DisplayName("메뉴 가격이 음수인 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {-10,-100})
    public void createWithValidPrice(int price) {
        // given
        Menu 음수_가격_후라이드_치킨_메뉴 = 후라이드_치킨_메뉴(price);

        // when, then
        assertThatThrownBy(() -> menuService.create(음수_가격_후라이드_치킨_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴는 등록되어 있는 메뉴 그룹에 포함되지 않을 경우 IllegalArgumentException을 던진다.")
    @Test
    public void createWithinRegisteredMenuGroup() {
        // given
        given(menuGroupRepository.findById(any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> menuService.create(기본_후라이드_치킨_메뉴))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴는 메뉴 상품을 가지지 않는 경우 IllegalArgumentException을 던진다.")
    @Test
    public void createMenuProductToBeRegisteredAsProduct() {
        // given
        Menu 메뉴상품_없는_후라이드_치킨_메뉴 = 후라이드_치킨_메뉴(Collections.emptyList());

        given(menuGroupRepository.findById(any()))
                .willReturn(Optional.of(기본_한마리_메뉴_그룹));

        // when, then
        assertThatThrownBy(() -> menuService.create(메뉴상품_없는_후라이드_치킨_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 상품이 모두 상품으로 등록되지 않는 경우 IllegalArgumentException을 던진다.")
    @Test
    public void createWithValidNumberOfMenuProduct() {
        // given
        Menu 후라이드_치킨_메뉴 = 후라이드_치킨_메뉴(Arrays.asList(
                후라이드_메뉴_상품( 1L)));

        given(menuGroupRepository.findById(any()))
                .willReturn(Optional.of(기본_한마리_메뉴_그룹));
        given(productRepository.findAllById(any()))
                .willReturn(Collections.emptyList());

        // when, then
        assertThatThrownBy(() -> menuService.create(후라이드_치킨_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 개수가 음수 일 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(longs = {-10l,-100l})
    public void createWithValidQuantity(long quantity) {
        // given
        Menu 후라이드_치킨_메뉴 = 후라이드_치킨_메뉴(Arrays.asList(
                후라이드_메뉴_상품(quantity)));

        given(menuGroupRepository.findById(any()))
                .willReturn(Optional.of(기본_한마리_메뉴_그룹));
        given(productRepository.findAllById(any()))
                .willReturn(Collections.emptyList());

        // when, then
        assertThatThrownBy(() -> menuService.create(후라이드_치킨_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 메뉴 상품들의 가격 * 수량 합을 넘을 경우 IllegalArgumentException을 던진다.")
    @Test
    public void createWithLowerPriceThanSumOfMenuProduct() {
        // given
        int menuPrice = 17000;
        Menu 후라이드_치킨_메뉴 = 후라이드_치킨_메뉴(menuPrice, Arrays.asList(
                후라이드_메뉴_상품(1L)));

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(기본_한마리_메뉴_그룹));
        given(productRepository.findAllById(any())).willReturn(Arrays.asList(기본_후라이드_상품));
        given(productRepository.findById(any())).willReturn(Optional.of(기본_후라이드_상품));

        // when, then
        assertThatThrownBy(() -> menuService.create(후라이드_치킨_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 이름은 욕설을 포함하는 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(strings = {"fuck", "shit"})
    public void createWithoutBadWord(String name) {
        // given
        given(productRepository.findAllById(any())).willReturn(Arrays.asList(기본_후라이드_상품));
        given(productRepository.findById(any())).willReturn(Optional.of(기본_후라이드_상품));
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(기본_한마리_메뉴_그룹));

        Menu 욕설_이름_메뉴 = 메뉴(name, 기본_후라이드_가격, Arrays.asList(
                후라이드_메뉴_상품(1L)));
        given(purgomalumClient.containsProfanity(anyString())).willReturn(true);

        // when, then
        assertThatThrownBy(() -> menuService.create(욕설_이름_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("변경되는 메뉴 가격이 음수인 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {-10, -100})
    public void changePriceWithValidPrice(int price) {
        // given
        Menu 후라이드_치킨_메뉴 = 후라이드_치킨_메뉴(price);

        // when, then
        assertThatThrownBy(() -> menuService.changePrice(id, 후라이드_치킨_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("변경되는 메뉴 가격은 메뉴 상품들의 (가격 * 수량) 합을 넘을 경우 " +
            "IllegalArgumentException을 던진다.")
    @Test
    public void changePriceWithLowerPriceThanSumOfMenuProduct() {
        // given
        int menuPrice = 18000;
        Menu 변경될_후라이드_치킨_메뉴 = 후라이드_치킨_메뉴(menuPrice, Arrays.asList(
                후라이드_메뉴_상품( 1l)));
        given(menuRepository.findById(id)).willReturn(Optional.of(기본_후라이드_치킨_메뉴));

        // when, then
        assertThatThrownBy(() -> menuService.changePrice(id, 변경될_후라이드_치킨_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록되지 않은 메뉴인 경우 NoSuchElementException을 던진다.")
    @Test
    public void displayWithOnlyRegisteredMenu() {
        // given
        given(menuRepository.findById(id)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> menuService.display(id))
                .isInstanceOf(NoSuchElementException.class);
    }


    @DisplayName("노출하려는 메뉴의 가격은 메뉴에 등록된 메뉴 상품의 가격의 합보다 크지 않은 경우 IllegalArgumentException을 던진다.")
    @Test
    public void displayMenuThatLessThanSumOfMenuProductPrice() {
        // given
        int menuPrice = 18000;
        int menuProductPrice = 17000;
        Menu 후라이드_치킨_메뉴 = 후라이드_치킨_메뉴(menuPrice,Arrays.asList(
                후라이드_메뉴_상품(menuProductPrice, 1l)));

        given(menuRepository.findById(id)).willReturn(Optional.of(후라이드_치킨_메뉴));

        // when, then
        assertThatThrownBy(() -> menuService.display(id))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("등록되지 않은 메뉴인 경우 IllegalArgumentException을 던진다.")
    @Test
    public void hideWithRegisteredMenu() {
        // given
        given(menuRepository.findById(id)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> menuService.hide(id))
                .isInstanceOf(NoSuchElementException.class);
    }
}
