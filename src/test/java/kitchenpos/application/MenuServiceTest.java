package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.NullString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.channels.IllegalChannelGroupException;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    private Menu menu;

    @BeforeEach
    void setup() {
        Product product = productRepository.findById(UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10"))
                .orElseThrow(NoSuchElementException::new);
        MenuGroup menuGroup = menuGroupRepository.findById(UUID.fromString("d9bc21ac-cc10-4593-b506-4a40e0170e02"))
                .orElse(null);
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1);
        menu = new Menu();
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setName("메뉴1");
        menu.setPrice(BigDecimal.valueOf(12000));
        menu.setMenuGroupId(Objects.requireNonNull(menuGroup).getId());
        menu.setMenuProducts(Collections.singletonList(menuProduct));
    }

    @DisplayName("메뉴가격이 null 이거나 0보다작으면 등록할 수 없다.")
    @NullSource
    @ValueSource(strings = {"-1", "-1000"})
    @ParameterizedTest
    void menuPriceNullOrEmptyTest(BigDecimal price) {
        menu.setPrice(price);
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴생성시 메뉴명과 가격을 포함해야 한다.")
    @MethodSource("provideNameAndPriceIsNullOrEmpty")
    @ParameterizedTest
    void menuNameAndPriceNullOrEmptyTest(String name, BigDecimal price) {
        menu.setName(name);
        menu.setPrice(null);
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴명에는 비속어는 사용할 수 없다.")
    @Test
    void menuCreateWithProfanity() {
        menu.setName("fuck");
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴생성시 메뉴가 포함될 메뉴그룹정보를 포함해야한다.")
    @ValueSource(strings = {"f59b1e1c-b145-440a-aa6f-6095a0e2d63b", "e1254913-8608-46aa-b23a-a07c1dcbc648"})
    @ParameterizedTest
    void invalidMenuGroupInfoTest(String menuGroupId) {
        menu.setMenuGroupId(UUID.fromString(menuGroupId));
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴생성시 해당 메뉴에 포함될 상품들에 대한 정보를 포함해야한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void nullAndEmptyMenuProductTest(List<MenuProduct> menuProducts) {
        menu.setMenuProducts(menuProducts);
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 등록한다.")
    @Test
    void menuCreateTest() {
        Menu actual = menuService.create(menu);
        assertThat(actual.getName()).isEqualTo("메뉴1");
        assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(12000));
    }

    @DisplayName("메뉴가격은 0보다 큰값이어야 한다.")
    @ValueSource(strings = {"-1", "-10000"})
    @ParameterizedTest
    void minusMenuPriceChangeTest(BigDecimal price) {
        Menu request = menuRepository.findById(UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b"))
                .orElseThrow(null);
        request.setPrice(price);
        assertThat(request).isNotNull();
        assertThatThrownBy(() -> menuService.changePrice(request.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 메뉴에 포함된 상품들의 총가격(단가 * 수량)보다 클수 없다.")
    @Test
    void totalMenuPriceTest() {
        // 후라이드치킨, 16000원
        Menu request = menuRepository.findById(UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b"))
                .orElseThrow(null);
        assertThat(request).isNotNull();
        request.setPrice(BigDecimal.valueOf(20000));
        assertThatThrownBy(() -> menuService.changePrice(request.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 숨긴다.")
    @Test
    void hideTest() {
        Menu request = menuRepository.findById(UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b"))
                .orElseThrow(null);
        assertThat(request).isNotNull();
        Menu actual = menuService.hide(request.getId());
        assertThat(actual.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴를 노출시킨다.")
    @Test
    void displayTest() {
        Menu request = menuRepository.findById(UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b"))
                .orElseThrow(null);
        assertThat(request).isNotNull();
        Menu actual = menuService.display(request.getId());
        assertThat(actual.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴목록을 조회한다.")
    @Test
    void findAllTest() {
        List<Menu> menus = menuService.findAll();
        assertThat(menus.size()).isEqualTo(13);
    }

    private static Stream<Arguments> provideNameAndPriceIsNullOrEmpty() {
        return Stream.of(Arguments.of(null, null),
                Arguments.of(null, BigDecimal.valueOf(1000)),
                Arguments.of("메뉴메뉴", null),
                Arguments.of("", BigDecimal.valueOf(1000)),
                Arguments.of("메뉴메뉴", BigDecimal.ZERO));
    }
}