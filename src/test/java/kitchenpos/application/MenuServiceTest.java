package kitchenpos.application;

import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.MenuGroupFixture;
import kitchenpos.application.fixture.MenuProductFixture;
import kitchenpos.application.fixture.ProductFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private MenuService menuService;

    @Test
    @DisplayName("메뉴를 생성한다.")
    void createMenu() {

        MenuGroup menuGroup = MenuGroupFixture.setMenuGroup("메인메뉴그룹");

        MenuProduct menuProduct =
                MenuProductFixture.setMenuProduct(
                        ProductFixture.setProduct("음식1", "10000")
                        , 1, 1
                );

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(menuProduct.getProduct()));
        given(productRepository.findById(any())).willReturn(Optional.of(menuProduct.getProduct()));

        Menu menu = MenuFixture.setMenuGroup(menuGroup, "메인디쉬", "10000", List.of(menuProduct));

        given(menuRepository.save(any())).willReturn(menu);

        Menu createdMenu = menuService.create(menu);

        assertAll(
                () -> assertThat(createdMenu.getMenuGroup()).isEqualTo(menuGroup),
                () -> assertThat(createdMenu.getName()).isEqualTo("메인디쉬"),
                () -> assertThat(createdMenu.getId()).isInstanceOf(UUID.class),
                () -> assertThat(createdMenu.getMenuProducts()).containsExactly(menuProduct)
        );
    }

    @Test
    @DisplayName("메뉴는 메뉴그룹을 필수로 가져야 한다.")
    void throwExceptionWithoutMenuGroup(){
        MenuProduct menuProduct =
                MenuProductFixture.setMenuProduct(
                        ProductFixture.setProduct("음식1", "10000")
                        , 1, 1
                );

        Menu menu = MenuFixture.setMenuGroup(null, "메인디쉬", "10000", List.of(menuProduct));

        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("가격(price) 은 필수로 입력되어야 하며, 0 이상의 양수값 이어야 한다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1"})
    void throwExceptionWhenPriceIsNullOrZero(String price){
        MenuGroup menuGroup = MenuGroupFixture.setMenuGroup("메인메뉴그룹");
        MenuProduct menuProduct =
                MenuProductFixture.setMenuProduct(
                        ProductFixture.setProduct("음식1", "10000")
                        , 1, 1
                );
        Menu menu = MenuFixture.setMenuGroup(menuGroup, "메인디쉬", price, List.of(menuProduct));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 이름(name)은 필수로 입력되어야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void throwExceptionWhenNameIsEmptyOrNull(String name){
        MenuGroup menuGroup = MenuGroupFixture.setMenuGroup("메인메뉴그룹");
        MenuProduct menuProduct =
                MenuProductFixture.setMenuProduct(
                        ProductFixture.setProduct("음식1", "10000")
                        , 1, 1
                );
        Menu menu = MenuFixture.setMenuGroup(menuGroup, name, "10000", List.of(menuProduct));

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(menuProduct.getProduct()));
        given(productRepository.findById(any())).willReturn(Optional.of(menuProduct.getProduct()));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 이름(name)은 필수로 입력되어야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"비속어"})
    void throwExceptionWhenNameIsProfanity(String name){
        MenuGroup menuGroup = MenuGroupFixture.setMenuGroup("메인메뉴그룹");
        MenuProduct menuProduct =
                MenuProductFixture.setMenuProduct(
                        ProductFixture.setProduct("음식1", "10000")
                        , 1, 1
                );
        Menu menu = MenuFixture.setMenuGroup(menuGroup, name, "10000", List.of(menuProduct));

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(menuProduct.getProduct()));
        given(productRepository.findById(any())).willReturn(Optional.of(menuProduct.getProduct()));
        given(purgomalumClient.containsProfanity(any())).willReturn(true);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격 변경시 0 이상의 양수값 이어야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1","-2000","-1000"})
    void throwExceptionWhenPriceIsNegative(String price){
        MenuGroup menuGroup = MenuGroupFixture.setMenuGroup("메인메뉴그룹");
        MenuProduct menuProduct =
                MenuProductFixture.setMenuProduct(
                        ProductFixture.setProduct("음식1", "10000")
                        , 1, 1
                );
        Menu menu = MenuFixture.setMenuGroup(menuGroup, "메인디쉬", price, List.of(menuProduct));

        assertThatThrownBy(() -> menuService.changePrice(UUID.randomUUID(), menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 변경이 가능하나, 포함한 메뉴 상품의 가격의 합보다 클수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"15000","20000","30000"})
    void menuPriceShouldBeSmallerThanProductPrice(String price){
        MenuGroup menuGroup = MenuGroupFixture.setMenuGroup("메인메뉴그룹");
        MenuProduct menuProduct =
                MenuProductFixture.setMenuProduct(
                        ProductFixture.setProduct("음식1", "10000")
                        , 1, 1
                );
        Menu menu = MenuFixture.setMenuGroup(menuGroup, "메인디쉬", price, List.of(menuProduct));

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        assertThatThrownBy(() -> menuService.changePrice(UUID.randomUUID(), menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴를 화면에 나타낸다.")
    void showMenu(){
        MenuGroup menuGroup = MenuGroupFixture.setMenuGroup("메인메뉴그룹");
        MenuProduct menuProduct =
                MenuProductFixture.setMenuProduct(
                        ProductFixture.setProduct("음식1", "10000")
                        , 1, 1
                );
        Menu menu = MenuFixture.setMenuGroup(menuGroup, "메인디쉬", "10000", List.of(menuProduct));
        menu.setDisplayed(false);

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        Menu menuResult = menuService.display(UUID.randomUUID());

        assertThat(menuResult.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("메뉴를 화면에 나타낼때 메뉴의 가격은 메뉴상품의 가격보다 클수 없다..")
    void cannotShowMenu(){
        MenuGroup menuGroup = MenuGroupFixture.setMenuGroup("메인메뉴그룹");
        MenuProduct menuProduct =
                MenuProductFixture.setMenuProduct(
                        ProductFixture.setProduct("음식1", "10000")
                        , 1, 1
                );
        Menu menu = MenuFixture.setMenuGroup(menuGroup, "메인디쉬", "20000", List.of(menuProduct));

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        assertThatThrownBy(() -> menuService.display(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("메뉴를 화면에 숨긴다.")
    void hideMenu(){
        MenuGroup menuGroup = MenuGroupFixture.setMenuGroup("메인메뉴그룹");
        MenuProduct menuProduct =
                MenuProductFixture.setMenuProduct(
                        ProductFixture.setProduct("음식1", "10000")
                        , 1, 1
                );
        Menu menu = MenuFixture.setMenuGroup(menuGroup, "메인디쉬", "10000", List.of(menuProduct));
        menu.setDisplayed(true);

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        Menu menuResult = menuService.hide(UUID.randomUUID());

        assertThat(menuResult.isDisplayed()).isFalse();
    }

}
