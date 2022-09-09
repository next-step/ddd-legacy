package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


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

    @BeforeEach
    void beforeEach() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @Test
    @DisplayName("메뉴를 조회한다")
    void finaMenu() {
        // given
        Menu menu1 = new Menu();
        Menu menu2 = new Menu();
        given(menuRepository.findAll()).willReturn(List.of(menu1, menu2));

        // when
        List<Menu> result = menuService.findAll();

        // then
        assertThat(result).containsExactly(menu1, menu2);
    }

    @Test
    @DisplayName("메뉴를 추가한다")
    void createMenu() {
        // given
        MenuGroup menuGroup = new MenuGroup();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(10);

        Product product = new Product();
        product.setPrice(BigDecimal.ONE);

        Menu menu = new Menu();
        menu.setName("잘못된이름");
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(menuProduct));

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
        given(productRepository.findById(any())).willReturn(Optional.of(product));
        given(purgomalumClient.containsProfanity(any())).willReturn(false);
        given(menuRepository.save(any())).willReturn(menu);

        // when
        Menu result = menuService.create(menu);

        // then
        assertThat(result).isEqualTo(menu);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1.1"})
    @DisplayName("가격이 없거나, 가격이 0원 이하이면 메뉴를 추가할 수 없다.")
    void createMenuNotPrice(BigDecimal input) {
        // given
        Menu menu = new Menu();
        menu.setPrice(input);

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
    }

    @Test
    @DisplayName("추가할 메뉴에 대해 카테고리가 존재하지 않으면 추가할 수 없다.")
    void createMenuNotFoundMenuGroup() {
        // given
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(new MenuGroup());

        given(menuGroupRepository.findById(any())).willReturn(Optional.empty());

        // then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> menuService.create(menu));
    }

    @Test
    @DisplayName("메뉴 상품 목록이 비어있으면 메뉴를 추가할 수 없다.")
    void createMenuNotFoundMenuProduct() {
        // given
        MenuGroup menuGroup = new MenuGroup();
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(menuGroup);

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
    }

    @Test
    @DisplayName(" 조회 한 상품 목록이 메뉴 상품 목록에 대해 갯수가 일치하지 않으면 메뉴를 추가할 수 없다.")
    void createMenuProductNotMatchedSizeProduct() {
        // given
        MenuGroup menuGroup = new MenuGroup();
        MenuProduct menuProduct = new MenuProduct();

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(List.of(menuProduct));

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of());

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
    }

    @Test
    @DisplayName("수량이 0 미만인 상품이 있다면 메뉴를 추가할 수 없다.")
    void createMenuProductQuantityIsZeroUnder() {
        // given
        MenuGroup menuGroup = new MenuGroup();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(-1);

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(List.of(menuProduct));

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(new Product()));

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
    }

    @Test
    @DisplayName("상품이 없다면 메뉴를 추가할 수 없다.")
    void createMenuProductNotFound() {
        // given
        MenuGroup menuGroup = new MenuGroup();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(10);

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(List.of(menuProduct));

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(new Product()));
        given(productRepository.findById(any())).willReturn(Optional.empty());

        // when
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> menuService.create(menu));
    }

    @Test
    @DisplayName(" 측정 된 가격이 0원 이하면 메뉴를 추가할 수 없다.")
    void createMenuProductSetPriceZeroUnder() {
        // given
        MenuGroup menuGroup = new MenuGroup();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(10);

        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(-1));

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(List.of(menuProduct));

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
        given(productRepository.findById(any())).willReturn(Optional.of(product));

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("메뉴 추가 시 이름이 비어있으면 생성할 수 없다")
    void menuCreateNotName(String input) {
        // given
        MenuGroup menuGroup = new MenuGroup();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(10);

        Product product = new Product();
        product.setPrice(BigDecimal.ONE);

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(List.of(menuProduct));
        menu.setName(input);

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
        given(productRepository.findById(any())).willReturn(Optional.of(product));

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                menuService.create(menu)
        );
    }

    @Test
    @DisplayName("카테고리 생성 시 이름이 비어있으면 생성할 수 없다")
    void menuCreateIsPurgomalum() {
        // given
        MenuGroup menuGroup = new MenuGroup();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(10);

        Product product = new Product();
        product.setPrice(BigDecimal.ONE);

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(List.of(menuProduct));
        menu.setName("잘못된이름");

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
        given(productRepository.findById(any())).willReturn(Optional.of(product));
        given(purgomalumClient.containsProfanity(any())).willReturn(true);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                menuService.create(menu)
        );
    }

    @Test
    @DisplayName("메뉴 가격을 변경한다")
    void changePrice() {
        // given
        Product product = new Product();
        product.setPrice(BigDecimal.ONE);

        MenuGroup menuGroup = new MenuGroup();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(10);
        menuProduct.setProduct(product);

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(List.of(menuProduct));
        menu.setName("잘못된이름");

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        Menu result = menuService.changePrice(menu.getId(), menu);

        // then
        assertThat(result.getPrice()).isEqualTo(BigDecimal.ONE);
    }


    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1"})
    @DisplayName("가격이 없거나, 가격이 0원 이하면 변경할 수 없다")
    void changePriceButNotPrice(BigDecimal input) {
        // given
        Product product = new Product();
        product.setPrice(BigDecimal.ONE);

        MenuGroup menuGroup = new MenuGroup();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(10);
        menuProduct.setProduct(product);

        Menu menu = new Menu();
        menu.setPrice(input);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(List.of(menuProduct));
        menu.setName("잘못된이름");

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                menuService.changePrice(menu.getId(), menu)
        );
    }

    @Test
    @DisplayName("메뉴가 존재하지 않으면 가격을 변경할 수 없다")
    void changePriceButNotExistedMenu() {
        // given
        Product product = new Product();
        product.setPrice(BigDecimal.ONE);

        MenuGroup menuGroup = new MenuGroup();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(10);
        menuProduct.setProduct(product);

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(List.of(menuProduct));
        menu.setName("잘못된이름");

        given(menuRepository.findById(any())).willReturn(Optional.empty());

        // then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() ->
                menuService.changePrice(menu.getId(), menu)
        );
    }

    @Test
    @DisplayName("메뉴의 가격이 메뉴의 수량에 대한 값보다 작으면 변경할 수 없다")
    void changePriceButSumLessThenPrice() {
        // given
        Product product = new Product();
        product.setPrice(BigDecimal.ONE);

        MenuGroup menuGroup = new MenuGroup();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(0);
        menuProduct.setProduct(product);

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(List.of(menuProduct));
        menu.setName("잘못된이름");

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                menuService.changePrice(menu.getId(), menu)
        );
    }

    @Test
    @DisplayName("비공개 된 메뉴를 공개할 수 있다")
    void changeDisplay() {
        // given
        Product product = new Product();
        product.setPrice(BigDecimal.ONE);

        MenuGroup menuGroup = new MenuGroup();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(10);
        menuProduct.setProduct(product);

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(List.of(menuProduct));
        menu.setName("잘못된이름");

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        Menu result = menuService.display(UUID.randomUUID());

        // then
        assertThat(result.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName(" 메뉴가 존재하지 않으면 메뉴를 공개할 수 없다.")
    void changeDisplayNotExistedMenu() {
        // given
        Product product = new Product();
        product.setPrice(BigDecimal.ONE);

        MenuGroup menuGroup = new MenuGroup();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(10);
        menuProduct.setProduct(product);

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(List.of(menuProduct));
        menu.setName("잘못된이름");

        given(menuRepository.findById(any())).willReturn(Optional.empty());

        // then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() ->
                menuService.display(UUID.randomUUID())
        );
    }

    @Test
    @DisplayName("메뉴의 가격이 메뉴의 수량에 대한 값보다 작으면 공개할 수 없다")
    void changeDisplayButSumLessThenPrice() {
        // given
        Product product = new Product();
        product.setPrice(BigDecimal.ONE);

        MenuGroup menuGroup = new MenuGroup();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(0);
        menuProduct.setProduct(product);

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(List.of(menuProduct));
        menu.setName("잘못된이름");

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() ->
                menuService.display(UUID.randomUUID())
        );
    }

    @Test
    @DisplayName("공개 된 메뉴를 비공개할 수 있다")
    void changeHide() {
        // given
        Product product = new Product();
        product.setPrice(BigDecimal.ONE);

        MenuGroup menuGroup = new MenuGroup();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(0);
        menuProduct.setProduct(product);

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(List.of(menuProduct));
        menu.setName("잘못된이름");
        menu.setDisplayed(true);

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        Menu result = menuService.hide(UUID.randomUUID());

        // then
        assertThat(result.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName(" 메뉴가 존재하지 않으면 메뉴를 비공개할 수 없다.")
    void changeHideNotExistedMenu() {
        // given
        Product product = new Product();
        product.setPrice(BigDecimal.ONE);

        MenuGroup menuGroup = new MenuGroup();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(10);
        menuProduct.setProduct(product);

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(List.of(menuProduct));
        menu.setName("잘못된이름");
        menu.setDisplayed(true);

        given(menuRepository.findById(any())).willReturn(Optional.empty());

        // then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() ->
                menuService.display(UUID.randomUUID())
        );
    }
}
