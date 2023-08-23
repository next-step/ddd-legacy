package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.test_fixture.MenuGroupTestFixture;
import kitchenpos.test_fixture.MenuProductTestFixture;
import kitchenpos.test_fixture.MenuTestFixture;
import kitchenpos.test_fixture.ProductTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("ProductService 클래스")
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService sut;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private EntityManager entityManager;


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
                .changeName("bastard") // `새끼` 라는 나쁜말 ^^
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
                .changePrice(BigDecimal.valueOf(2000))
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
        productRepository.save(product);
        Menu menu = this.createMenu(product);

        Product changePriceRequest = ProductTestFixture.create()
                .changeId(product.getId())
                .changePrice(BigDecimal.valueOf(1999))
                .getProduct();

        // when
        sut.changePrice(product.getId(), changePriceRequest);

        // then
        Menu menuWithProduct = menuRepository.findById(menu.getId()).get();
        assertFalse(menuWithProduct.isDisplayed());
    }

    private Menu createMenu(Product product) {
        MenuGroup menuGroup = MenuGroupTestFixture.create()
                .changeId(UUID.randomUUID())
                .getMenuGroup();
        menuGroupRepository.save(menuGroup);
        MenuProduct menuProduct = MenuProductTestFixture.create()
                .changeProduct(product)
                .getMenuProduct();
        Menu menu = MenuTestFixture.create()
                .changeId(UUID.randomUUID())
                .changePrice(BigDecimal.valueOf(2000))
                .changeMenuGroup(menuGroup)
                .changeMenuProducts(menuProduct)
                .changeDisplayed(true)
                .getMenu();
        return menuRepository.save(menu);
    }
}

