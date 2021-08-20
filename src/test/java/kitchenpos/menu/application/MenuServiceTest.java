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

import java.math.BigDecimal;
import java.util.*;

import static kitchenpos.menu.fixture.MenuFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
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

    private String 후라이드;
    private int 후라이드_가격;
    private String 후라이드_치킨;
    private int 후라이드_치킨_가격;
    private long 한개;

    private Menu 메뉴_생성_요청;
    private MenuGroup 한마리_메뉴_그룹;
    private Product 후라이드_상품;
    private Menu 후라이드_한마리_메뉴;
    private MenuProduct 후라이드_한마리_메뉴_상품;

    private UUID id;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
        후라이드 = "후라이드";
        후라이드_치킨 = "후라이드치킨";
        후라이드_가격 = 16000;
        후라이드_치킨_가격 = 16000;
        한개 = 1L;

        메뉴_생성_요청 = 메뉴_생성_요청(후라이드_치킨, 후라이드_가격, true, Arrays.asList(메뉴_상품_생성_요청(한개)));
        한마리_메뉴_그룹 = 메뉴_그룹("한마리메뉴");
        후라이드_상품 = 상품(후라이드, 후라이드_가격);
        후라이드_한마리_메뉴_상품 = 메뉴_상품(1L, 한개, 후라이드_상품);
        후라이드_한마리_메뉴 = 메뉴(후라이드_치킨, 후라이드_치킨_가격, true, 한마리_메뉴_그룹, Arrays.asList(후라이드_한마리_메뉴_상품));
        id = UUID.randomUUID();
    }

    @DisplayName("메뉴를 등록한다.")
    @Test
    public void create() {
        // given
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(한마리_메뉴_그룹));
        given(productRepository.findAllById(any())).willReturn(Arrays.asList(후라이드_상품));
        given(productRepository.findById(any())).willReturn(Optional.of(후라이드_상품));
        given(purgomalumClient.containsProfanity(anyString())).willReturn(false);
        given(menuRepository.save(any())).willReturn(후라이드_한마리_메뉴);

        // when
        Menu createdMenu = menuService.create(메뉴_생성_요청);

        // then
        assertAll(
                () -> assertThat(createdMenu.getName()).isEqualTo(후라이드_치킨),
                () -> assertThat(createdMenu.getPrice()).isEqualTo(new BigDecimal(후라이드_치킨_가격)),
                () -> assertThat(createdMenu.isDisplayed()).isEqualTo(true),
                () -> assertThat(createdMenu.getMenuProducts().size()).isEqualTo(1),
                () -> assertThat(createdMenu.getMenuProducts().get(0).getQuantity()).isEqualTo(한개),
                () -> assertThat(createdMenu.getMenuProducts().get(0).getProduct().getPrice()).isEqualTo(new BigDecimal(후라이드_가격)),
                () -> assertThat(createdMenu.getMenuProducts().get(0).getProduct().getName()).isEqualTo(후라이드)
        );
    }

    @DisplayName("메뉴 등록 시 메뉴 가격이 음수인 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {-10,-100})
    public void createWithValidPrice(int price) {
        // given
        Menu 음수_가격_메뉴_요청 = 메뉴_생성_요청(후라이드_치킨, price, true, null);

        // when, then
        assertThatThrownBy(() -> menuService.create(음수_가격_메뉴_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 등록 시 등록되어 있는 메뉴 그룹에 포함되지 않을 경우 IllegalArgumentException을 던진다.")
    @Test
    public void createWithinRegisteredMenuGroup() {
        // given
        given(menuGroupRepository.findById(any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> menuService.create(메뉴_생성_요청))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 등록 시 메뉴 상품을 가지지 않는 경우 IllegalArgumentException을 던진다.")
    @Test
    public void createMenuProductToBeRegisteredAsProduct() {
        // given
        List<MenuProduct> 빈_메뉴_상품 = Collections.emptyList();
        Menu 메뉴상품_없는_메뉴 = 메뉴_생성_요청(후라이드_치킨, 후라이드_치킨_가격, true, 빈_메뉴_상품);

        given(menuGroupRepository.findById(any()))
                .willReturn(Optional.of(한마리_메뉴_그룹));

        // when, then
        assertThatThrownBy(() -> menuService.create(메뉴상품_없는_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 등록 시 메뉴 상품이 모두 상품으로 등록되지 않는 경우 IllegalArgumentException을 던진다.")
    @Test
    public void createWithValidNumberOfMenuProduct() {
        // given
        List<Product> 빈_상품 = Collections.emptyList();

        given(menuGroupRepository.findById(any()))
                .willReturn(Optional.of(한마리_메뉴_그룹));
        given(productRepository.findAllById(any()))
                .willReturn(빈_상품);

        // when, then
        assertThatThrownBy(() -> menuService.create(메뉴_생성_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 등록 시 상품 개수가 음수 일 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(longs = {-10l,-100l})
    public void createWithValidQuantity(long quantity) {
        // given
        Menu 상품_개수가_음수인_메뉴_생성_요청 = 메뉴_생성_요청(후라이드_치킨, 후라이드_치킨_가격, true, Arrays.asList(
                메뉴_상품(1L, quantity, 후라이드_상품)));

        given(menuGroupRepository.findById(any()))
                .willReturn(Optional.of(한마리_메뉴_그룹));
        given(productRepository.findAllById(any()))
                .willReturn(Collections.emptyList());

        // when, then
        assertThatThrownBy(() -> menuService.create(상품_개수가_음수인_메뉴_생성_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 등록 시 메뉴 가격이 메뉴 상품들의 가격 * 수량 합을 넘을 경우 IllegalArgumentException을 던진다.")
    @Test
    public void createWithLowerPriceThanSumOfMenuProduct() {
        // given
        int menuPrice = 17000;
        Menu 후라이드_치킨_메뉴 = 메뉴_생성_요청(후라이드, menuPrice, true, Arrays.asList(후라이드_한마리_메뉴_상품));

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(한마리_메뉴_그룹));
        given(productRepository.findAllById(any())).willReturn(Arrays.asList(후라이드_상품));
        given(productRepository.findById(any())).willReturn(Optional.of(후라이드_상품));

        // when, then
        assertThatThrownBy(() -> menuService.create(후라이드_치킨_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 등록 시 메뉴 이름은 욕설을 포함하는 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(strings = {"fuck", "shit"})
    public void createWithoutBadWord(String name) {
        // given
        Menu 욕설_이름_메뉴_생성_요청 = 메뉴_생성_요청(name, 후라이드_가격, true, Arrays.asList(후라이드_한마리_메뉴_상품));

        given(productRepository.findAllById(any())).willReturn(Arrays.asList(후라이드_상품));
        given(productRepository.findById(any())).willReturn(Optional.of(후라이드_상품));
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(한마리_메뉴_그룹));
        given(purgomalumClient.containsProfanity(anyString())).willReturn(true);

        // when, then
        assertThatThrownBy(() -> menuService.create(욕설_이름_메뉴_생성_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("변경되는 메뉴 가격이 음수인 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {-10, -100})
    public void changePriceWithValidPrice(int price) {
        assertThatThrownBy(() -> menuService.changePrice(id, 메뉴_가격_변경_요청(price)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("변경되는 메뉴 가격은 메뉴 상품들의 (가격 * 수량) 합을 넘을 경우 IllegalArgumentException을 던진다.")
    @Test
    public void changePriceWithLowerPriceThanSumOfMenuProduct() {
        // given
        Menu 메뉴_가격_변경_요청 = 메뉴_가격_변경_요청(18000);

        given(menuRepository.findById(id)).willReturn(Optional.of(후라이드_한마리_메뉴));

        // when, then
        assertThatThrownBy(() -> menuService.changePrice(id, 메뉴_가격_변경_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 노출 시 등록되지 않은 메뉴인 경우 NoSuchElementException을 던진다.")
    @Test
    public void displayWithOnlyRegisteredMenu() {
        // given
        given(menuRepository.findById(id)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> menuService.display(id))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("노출하려는 메뉴의 가격은 메뉴에 등록된 메뉴 상품의 가격의 합보다 크지 않은 경우 IllegalStateException을 던진다.")
    @Test
    public void displayMenuThatLessThanSumOfMenuProductPrice() {
        // given
        int 메뉴_가격 = 18000;
        int 메뉴_상품_가격 = 17000;
        Menu 메뉴 = 메뉴(후라이드_치킨, 메뉴_가격, true, 한마리_메뉴_그룹, Arrays.asList(
                메뉴_상품(1L, 한개, 상품(후라이드, 메뉴_상품_가격))));

        given(menuRepository.findById(id)).willReturn(Optional.of(메뉴));

        // when, then
        assertThatThrownBy(() -> menuService.display(id))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("메뉴를 숨길 시 등록되지 않은 메뉴인 경우 NoSuchElementException을 던진다.")
    @Test
    public void hideWithRegisteredMenu() {
        // given
        given(menuRepository.findById(id)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> menuService.hide(id))
                .isInstanceOf(NoSuchElementException.class);
    }
}
