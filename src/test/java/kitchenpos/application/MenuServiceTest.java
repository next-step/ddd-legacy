package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

//@DisplayName("[메뉴]")
//@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
//class MenuServiceTest {
//
//    @Mock
//    private MenuRepository menuRepository;
//
//    @Mock
//    private MenuGroupRepository menuGroupRepository;
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private PurgomalumClient purgomalumClient;
//
//    @InjectMocks
//    private MenuService menuService;
//
//    @Test
//    @DisplayName("메뉴의 가격은 0보다 크거나 같아야 한다")
//    public void menuPriceLessThanZeroTest() {
//        Menu menu = new Menu();
//        menu.setPrice(BigDecimal.valueOf(0));
//
//        AssertionsForClassTypes.assertThatThrownBy(() -> menuService.create(menu))
//                .isInstanceOf(IllegalArgumentException.class);
//    }

//    @Test
//    @DisplayName("메뉴는 메뉴그룹에 속해 있어야 한다.")
//    public void menuInMenuGroupTest() {
//        Menu menu = MenuFixture.빈_메뉴_그룹();
//
//        AssertionsForClassTypes.assertThatThrownBy(() -> menuService.create(menu))
//                .isInstanceOf(NoSuchElementException.class);
//    }
//
//    @Test
//    @DisplayName("메뉴 상품은 하나 이상의 상품이 있어야 한다.")
//    public void mustBeAtLeastOneProductTest() {
//        UUID uuid = UUID.randomUUID();
//        MenuGroup menuGroup = new MenuGroup(uuid, "한마리 메뉴");
//
//        Menu menu = MenuFixture.빈_메뉴_상품(menuGroup);
//        given(menuGroupRepository.findById(uuid)).willReturn(Optional.of(menuGroup));
//
//        AssertionsForClassTypes.assertThatThrownBy(() -> menuService.create(menu))
//                .isInstanceOf(IllegalArgumentException.class);
//    }
//
//    @Test
//    @DisplayName("메뉴 상품의 갯수는 상품의 갯수와 같아야 한다.")
//    public void menuGroupEqualsToProduct() {
//        Menu menu = MenuFixture.메뉴();
//        MenuGroup menuGroup = MenuGroupFixture.메뉴_그룹(UUID.randomUUID(), "한마리 메뉴");
//        menu.setMenuGroup(menuGroup);
//
//        Product product = ProductFixture.상품(UUID.randomUUID(), "후라이드", BigDecimal.valueOf(16000L));
//        Product product2 = ProductFixture.상품(UUID.randomUUID(), "양념 치킨", BigDecimal.valueOf(16000L));
//
//        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
//        given(productRepository.findAllByIdIn(any())).willReturn(Arrays.asList(product, product2));
//
//        AssertionsForClassTypes.assertThatThrownBy(() -> menuService.create(menu))
//                .isInstanceOf(IllegalArgumentException.class);
//    }
//
//    @Test
//    @DisplayName("메뉴 이름이 있어야한다")
//    public void requiredMenuName() {
//        Menu menu = MenuFixture.빈_메뉴_이름();
//        MenuGroup menuGroup = MenuGroupFixture.메뉴_그룹(UUID.randomUUID(), "한마리 메뉴");
//        menu.setMenuGroup(menuGroup);
//
//        Product product = ProductFixture.상품(UUID.randomUUID(), "후라이드", BigDecimal.valueOf(16000L));
//
//        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
//        given(productRepository.findAllByIdIn(any())).willReturn(Arrays.asList(product));
//        given(productRepository.findById(any())).willReturn(Optional.of(product));
//
//        AssertionsForClassTypes.assertThatThrownBy(() -> menuService.create(menu))
//                .isInstanceOf(IllegalArgumentException.class);
//    }
//
//    @Test
//    @DisplayName("메뉴 이름에는 비속어가 포함될 수 없다.")
//    public void notContainsProfanityMenuName() {
//        Menu menu = MenuFixture.비속어_메뉴_이름();
//        MenuGroup menuGroup = MenuGroupFixture.메뉴_그룹(UUID.randomUUID(), "한마리 메뉴");
//        menu.setMenuGroup(menuGroup);
//
//        Product product = ProductFixture.상품(UUID.randomUUID(), "후라이드", BigDecimal.valueOf(16000L));
//
//        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
//        given(productRepository.findAllByIdIn(any())).willReturn(Arrays.asList(product));
//        given(productRepository.findById(any())).willReturn(Optional.of(product));
//
//        AssertionsForClassTypes.assertThatThrownBy(() -> menuService.create(menu))
//                .isInstanceOf(IllegalArgumentException.class);
//    }
//
//    @Test
//    @DisplayName("메뉴를 등록한다")
//    public void createMenu() {
//        Menu menuRequest = MenuFixture.정상_메뉴_요청();
//        MenuGroup menuGroup = MenuGroupFixture.메뉴_그룹(UUID.randomUUID(), "한마리 메뉴");
//        menuRequest.setMenuGroup(menuGroup);
//
//        Product product = ProductFixture.상품(UUID.randomUUID(), "후라이드", BigDecimal.valueOf(16000L));
//
//        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
//        given(productRepository.findAllByIdIn(any())).willReturn(Arrays.asList(product));
//        given(productRepository.findById(any())).willReturn(Optional.of(product));
//        given(menuRepository.save(any())).willReturn(menuRequest);
//
//        Menu menu = menuService.create(menuRequest);
//
//        assertThat(menu.getName()).isEqualTo(menuRequest.getName());
//        assertThat(menu.getPrice()).isEqualTo(menuRequest.getPrice());
//        assertThat(menu.getMenuGroupId()).isEqualTo(menuRequest.getMenuGroupId());
//    }
//
//    @Test
//    @DisplayName("메뉴의 가격이 각 메뉴상품의 갯수의 합보다 작다면 노출이 된다.")
//    public void displayTest() {
//
//        Menu menu = MenuFixture.등록된_메뉴_요청();
//        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
//        Menu display = menuService.display(menu.getId());
//
//        assertThat(display.isDisplayed()).isTrue();
//    }
//
//    @Test
//    @DisplayName("메뉴를 숨김처리 할 수 있다")
//    public void hideTest() {
//
//        Menu menu = MenuFixture.노출중인_메뉴();
//        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
//        Menu display = menuService.hide(menu.getId());
//
//        assertThat(display.isDisplayed()).isFalse();
//    }
//}