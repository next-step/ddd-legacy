package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest extends AbstractApplicationServiceTest {

    private static final String TEST_NAME = "dummyName";
    private static final BigDecimal TEST_PRICE = BigDecimal.valueOf(1_000L);

    @Mock
    private PurgomalumClient mockClient;

    private ProductRepository productRepository;
    private MenuRepository menuRepository;

    private ProductService service;

    @BeforeEach
    void setUp() {
        productRepository = new ProductFakeRepository();
        menuRepository = new MenuFakeRepository();
        service = new ProductService(productRepository, menuRepository, mockClient);
    }

    @DisplayName("product를 생성 후 반환한다.")
    @Test
    void create_success() {

        // given
        final Product request = createProductRequest(TEST_NAME, TEST_PRICE);

        // when
        final Product actual = service.create(request);

        // then
        assertThat(actual.getId()).isNotNull();
    }

    @DisplayName("product의 가격이 없거나 음수이면 예외를 발생시킨다.")
    @Test
    void create_invalid_price() {

        // given
        final Product nullPrice = createProductRequest(TEST_NAME, null);
        final Product negativePrice = createProductRequest(TEST_NAME, BigDecimal.valueOf(-1L));

        // when & then
        assertThatThrownBy(() -> service.create(nullPrice))
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> service.create(negativePrice))
            .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("product의 이름이 없거나 비속어가 포함되어 있으면 예외를 발생시킨다.")
    @Test
    void create_invalid_name_1() {

        // given
        final Product nullName = createProductRequest(null, TEST_PRICE);

        // when & then
        assertThatThrownBy(() -> service.create(nullName))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("product의 이름에 비속어가 포함되어 있으면 예외를 발생시킨다.")
    @Test
    void create_invalid_name_2() {

        // given
        final Product profanityName = createProductRequest("비속어", TEST_PRICE);
        doReturn(true)
            .when(mockClient)
            .containsProfanity(profanityName.getName());

        // when & then
        assertThatThrownBy(() -> service.create(profanityName))
            .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("product의 가격을 변경시킬 수 있다.")
    @Test
    void changePrice_success() {

        // given
        final Product product = productRepository.save(
            createProductRequest("product", BigDecimal.valueOf(2_000L)));
        final Product request = createProductRequest(product.getName(), BigDecimal.valueOf(3_000L));

        // when
        final Product actual = service.changePrice(product.getId(), request);

        // then
        assertThat(actual.getPrice())
            .isEqualTo(request.getPrice());

    }


    @DisplayName("변경 가격이 없거나 음수이면 예외를 발생시킨다.")
    @Test
    void changePrice_invalid_price() {
        // given
        final Product nullPrice = createProductRequest(TEST_NAME, null);
        final Product negativePrice = createProductRequest(TEST_NAME, BigDecimal.valueOf(-1L));

        // when & then
        assertThatThrownBy(() -> service.changePrice(UUID.randomUUID(), nullPrice));
        assertThatThrownBy(() -> service.changePrice(UUID.randomUUID(), negativePrice));
    }


    @DisplayName("가격 변경시 product가 존재하지 않으면 예외를 발생시킨다.")
    @Test
    void changePrice_invalid_productId() {
        // given
        final Product request = createProductRequest(TEST_NAME, TEST_PRICE);

        // when & then
        assertThatThrownBy(() -> service.changePrice(UUID.randomUUID(), request))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("product의 가격을 변경시키면 해당 product가 포함된 메뉴에 대해 product 가격의 합보다 메뉴의 가격이 크면 메뉴가 숨겨진다.")
    @Test
    void changePrice_success_with_menu_display() {
        // given
        final Product product = productRepository.save(
            createProductRequest(TEST_NAME, BigDecimal.valueOf(3_000L)));
        menuRepository.save(createMenuHasOneMenuProduct(BigDecimal.valueOf(1_000L), product));

        // when
        service.changePrice(product.getId(),
            createProductRequest(TEST_NAME, BigDecimal.valueOf(500L)));

        // then
        final Menu actual = menuRepository.findAllByProductId(product.getId()).get(0);
        assertThat(actual.isDisplayed()).isFalse();
    }

    private Menu createMenuHasOneMenuProduct(final BigDecimal menuPrice, final Product product) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(1);

        final Menu menu = new Menu();
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(ImmutableList.of(menuProduct));

        return menu;
    }

    @DisplayName("product들을 조회하여 반환한다")
    @Test
    void findAll() {
        // given
        final Product dummy1 = productRepository.save(
            createProductRequest("dummy1", BigDecimal.valueOf(1_000L)));
        final Product dummy2 = productRepository.save(
            createProductRequest("dummy2", BigDecimal.valueOf(3_000L)));

        // when
        final List<Product> actual = service.findAll();

        // then
        assertThat(actual)
            .usingRecursiveFieldByFieldElementComparator()
            .contains(dummy1, dummy2);
    }
}
