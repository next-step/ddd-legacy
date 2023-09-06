package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.FakeProfanityClient;
import kitchenpos.infra.ProfanityClient;
import kitchenpos.integration_test_step.MenuIntegrationStep;
import kitchenpos.test_fixture.ProductTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ProductService 클래스")
class ProductServiceTest {

    private ProductService sut;
    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProfanityClient profanityClient;
    private MenuIntegrationStep menuIntegrationStep;

    @BeforeEach
    void setUp() {
        productRepository = new FakeProductRepository();
        menuRepository = new FakeMenuRepository();
        menuGroupRepository = new FakeMenuGroupRepository();
        menuIntegrationStep = new MenuIntegrationStep(menuRepository, menuGroupRepository, productRepository);
        profanityClient = new FakeProfanityClient();
        sut = new ProductService(productRepository, menuRepository, profanityClient);
    }


    @DisplayName("새로운 상품을 등록할 수 있다.")
    @Test
    void create() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(null)
                .getProduct();

        // when
        Product result = sut.create(product);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트 상품");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(1000));
    }

    @DisplayName("새로운 상품을 등록할 때 가격이 null이면 예외가 발생한다.")
    @Test
    void createWithNullPrice() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(null)
                .changePrice(null)
                .getProduct();

        // when then
        assertThatThrownBy(() -> sut.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("새로운 상품을 등록할 때 가격이 0보다 작으면 예외가 발생한다.")
    @Test
    void createWithNegativePrice() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(null)
                .changePrice(BigDecimal.valueOf(-1))
                .getProduct();

        // when then
        assertThatThrownBy(() -> sut.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("새로운 상품을 등록할 때 이름이 null이면 예외가 발생한다.")
    @Test
    void createWithNullName() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(null)
                .changeName(null)
                .getProduct();

        // when then
        assertThatThrownBy(() -> sut.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("새로운 상품을 등록할 때 이름에 비속어가 포함되면 예외가 발생한다.")
    @Test
    void createWithProfanityName() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(null)
                .changeName("욕설이포함된이름") // `새끼` 라는 나쁜말 ^^
                .getProduct();

        // when then
        assertThatThrownBy(() -> sut.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격을 변경할 수 있다.")
    @Test
    void changePrice() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(UUID.randomUUID())
                .getProduct();
        productRepository.save(product);
        Product changePriceRequest = ProductTestFixture.create()
                .changeId(product.getId())
                .changePrice(BigDecimal.valueOf(2000))
                .getProduct();

        // when
        Product result = sut.changePrice(product.getId(), changePriceRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(product.getId());
        assertThat(result.getName()).isEqualTo("테스트 상품");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(2000));
    }

    @DisplayName("상품의 가격을 변경할 때 가격이 null이면 예외가 발생한다.")
    @Test
    void changePriceWithNullPrice() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(UUID.randomUUID())
                .getProduct();
        productRepository.save(product);
        Product changePriceRequest = ProductTestFixture.create()
                .changeId(product.getId())
                .changePrice(null)
                .getProduct();

        // when then
        assertThatThrownBy(() -> sut.changePrice(product.getId(), changePriceRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격을 변경할 때 가격이 0보다 작으면 예외가 발생한다.")
    @Test
    void changePriceWithNegativePrice() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(UUID.randomUUID())
                .getProduct();
        productRepository.save(product);
        Product changePriceRequest = ProductTestFixture.create()
                .changeId(product.getId())
                .changePrice(BigDecimal.valueOf(-1))
                .getProduct();

        // when then
        assertThatThrownBy(() -> sut.changePrice(product.getId(), changePriceRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격을 변경할 때 상품이 존재하지 않으면 예외가 발생한다.")
    @Test
    void changePriceWithNonExistentProduct() {
        // given
        UUID id = UUID.randomUUID();
        Product changePriceRequest = ProductTestFixture.create()
                .changeId(id)
                .getProduct();

        // when then
        assertThatThrownBy(() -> sut.changePrice(id, changePriceRequest))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("상품의 가격을 변경할 때 (변경하려는 상품의 가격 > 해당 상품이 포함된 메뉴의 가격)라면 해당 메뉴는 전시되지 않도록 변경한다.")
    @Test
    void changePriceWithMenuPriceHigherThanProductPrice() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(UUID.randomUUID())
                .getProduct();
        Product savedProduct = productRepository.save(product);
        Menu menu = menuIntegrationStep.createPersistMenu(savedProduct);

        Product changePriceRequest = ProductTestFixture.create()
                .changeId(savedProduct.getId())
                .changePrice(menu.getPrice().subtract(BigDecimal.ONE))
                .getProduct();

        // when
        sut.changePrice(savedProduct.getId(), changePriceRequest);

        // then
        Menu menuWithProduct = menuRepository.findById(menu.getId()).get();
        assertFalse(menuWithProduct.isDisplayed());
    }

    @DisplayName("전체 상품의 정보를 조회할 수 있다")
    @Test
    void findAll() {
        // given
        Product product = ProductTestFixture.create()
                .changeId(UUID.randomUUID())
                .getProduct();
        productRepository.save(product);
        Product product2 = ProductTestFixture.create()
                .changeId(UUID.randomUUID())
                .getProduct();
        productRepository.save(product2);

        // when
        List<Product> result = sut.findAll();

        // then
        assertThat(result)
                .isNotNull()
                .hasSize(2);
        result.forEach(it -> assertProduct(it, "테스트 상품", BigDecimal.valueOf(1000.00)));
    }

    private void assertProduct(Product product, String name, BigDecimal price) {
        assertThat(product).isNotNull();
        assertThat(product.getId()).isNotNull();
        assertThat(product.getName()).isEqualTo(name);
        assertTrue(product.getPrice().compareTo(price) == 0);
    }
}
