package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @InjectMocks
    private MenuService menuService;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @ParameterizedTest
    @DisplayName("메뉴 등록시 가격은 필수다")
    @MethodSource("nullAndNegativePrice")
    void nullAndNegativePrice(BigDecimal price) {
        Menu request = new Menu();
        request.setPrice(price);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(request));
    }

    @Test
    @DisplayName("메뉴 등록시 메뉴그룹은 필수다")
    void menuGroupIsRequired() {
        Menu request = new Menu();
        request.setPrice(BigDecimal.ZERO);

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @ParameterizedTest
    @DisplayName("메뉴 등록시 메뉴상품은 한개이상")
    @MethodSource("nullAndEmptyMenuProducts")
    void menuProductIsRequired(List<MenuProduct> menuProducts) {
        Menu request = new Menu();
        request.setPrice(BigDecimal.ZERO);
        request.setMenuProducts(menuProducts);

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(request));
    }

    @Test
    @DisplayName("메뉴 안에 메뉴상품은 모두 상품으로 존재해야 한다")
    void checkMenuProductsSizeAndProductsSize() {
        Menu request = new Menu();
        request.setPrice(BigDecimal.ZERO);
        request.setMenuProducts(List.of(new MenuProduct()));

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴상품의 수는 음수가 될 수 없다")
    @Test
    void negativeMenuProductQuantity() {
        Menu request = new Menu();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(-1);
        request.setPrice(BigDecimal.ZERO);
        request.setMenuProducts(List.of(menuProduct));

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(new Product()));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(request));
    }

    @Test
    @DisplayName("메뉴상품에 맞는 상품이 있어야 한다")
    void menuProductShouldHaveMatchingProduct() {
        Menu request = new Menu();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(0);
        request.setPrice(BigDecimal.ZERO);
        request.setMenuProducts(List.of(menuProduct));

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(new Product()));
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("메뉴의 가격은 메뉴상품 가격의 합보다 클 수 없다")
    void priceCannotExceedSumOfMenuProductPrices() {
        Menu request = new Menu();
        request.setPrice(BigDecimal.TEN);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(0);
        request.setMenuProducts(List.of(menuProduct));

        Product product = new Product();
        product.setPrice(BigDecimal.ZERO);

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(request));
    }

    @Test
    @DisplayName("이름은 null 일 수 없다")
    void nullName() {
        Menu request = new Menu();
        request.setPrice(BigDecimal.ZERO);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(0);
        request.setMenuProducts(List.of(menuProduct));

        Product product = new Product();
        product.setPrice(BigDecimal.ZERO);

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(request));
    }

    @Test
    @DisplayName("이름은 비속어 일 수 없다")
    void profanityName() {
        Menu request = new Menu();
        request.setPrice(BigDecimal.ZERO);
        request.setName("비속어");

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(0);
        request.setMenuProducts(List.of(menuProduct));

        Product product = new Product();
        product.setPrice(BigDecimal.ZERO);

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(purgomalumClient.containsProfanity(any())).thenReturn(true);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(request));
    }

    @Test
    @DisplayName("저장 등록 성공")
    void succeedSave() {
        Menu request = new Menu();
        request.setPrice(BigDecimal.ZERO);
        request.setName("메뉴이름");

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(0);
        request.setMenuProducts(List.of(menuProduct));

        Product product = new Product();
        product.setPrice(BigDecimal.ZERO);
        MenuGroup menuGroup = new MenuGroup();

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(purgomalumClient.containsProfanity(any())).thenReturn(false);
        when(menuRepository.save(any())).then(invocationOnMock -> invocationOnMock.getArgument(0));

        Menu saved = menuService.create(request);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("메뉴이름");
        assertThat(saved.getPrice()).isEqualTo(BigDecimal.ZERO);
        assertThat(saved.getMenuGroup()).isEqualTo(menuGroup);
        assertThat(saved.isDisplayed()).isFalse();
        assertThat(saved.getMenuProducts()).hasSize(1);

    }

    @Test
    void changePrice() {
    }

    @Test
    void display() {
    }

    @Test
    void hide() {
    }

    @Test
    void findAll() {
    }

    static Stream<Arguments> nullAndNegativePrice() {
        return Stream.of(
                null,
                Arguments.of(BigDecimal.valueOf(-1))
        );
    }

    static Stream<Arguments> nullAndEmptyMenuProducts() {
        return Stream.of(
                null,
                Arguments.of(List.of())
        );
    }
}
