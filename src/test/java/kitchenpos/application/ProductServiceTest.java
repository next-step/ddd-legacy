package kitchenpos.application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.transaction.Transactional;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private ProductService productService;
    @DisplayName("상품을 생성 할 수 있다.")
    @Test
    void create() {
        //given
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(17000));
        product.setName("간장치킨");

        //when
        Product result = productService.create(product);

        //then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo("간장치킨");
        Assertions.assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(17000));

    }

    @DisplayName("상품 생성 시, 상품 가격이 null이면  IllegalArgumentException 예외를 발생 한다.")
    @Test
    void canNotCreateProductWithNullPrice() {
        //given
        Product productWithNullPrice = new Product();
        productWithNullPrice.setPrice(null);

        //when

        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()->productService.create(productWithNullPrice));
    }

    @DisplayName("상품 생성 시, 상품 가격이 0보다 작으면  IllegalArgumentException 예외를 발생 한다.")
    @Test
    void canNotCreateProductWithEmptyPrice() {
        //given
        Product productWithNullPrice = new Product();
        productWithNullPrice.setPrice(BigDecimal.valueOf(-1));

        //when

        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()->productService.create(productWithNullPrice));
    }

    @DisplayName("상품의 가격을 변경 할 수 있다.")
    @Test
    void changePrice() {
        // given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("한마리메뉴");
        menuGroupRepository.save(menuGroup);

        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setPrice(BigDecimal.valueOf(17000));
        product1.setName("간장치킨");
        productRepository.save(product1);

        Product changedProduct = new Product();
        changedProduct.setPrice(BigDecimal.valueOf(18000));

        // when
        Product result = productService.changePrice(product1.getId(), changedProduct);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(18000));

    }

    @DisplayName("상품의 가격을 변경시, 변경할 가격이 null 이면 IllegalArgumentException 예외를 발생한다.")
    @Test
    void canNotChangeProductPriceWithNullPrice() {
        //given
        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setPrice(BigDecimal.valueOf(17000));
        product1.setName("간장치킨");
        productRepository.save(product1);

        Product productWithNullPrice = new Product();
        productWithNullPrice.setPrice(null);

        //when

        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()->productService.changePrice(product1.getId(),productWithNullPrice));
    }

    @DisplayName("상품의 가격을 변경시, 변경할 가격이 0원 보다 작으면 IllegalArgumentException 예외를 발생한다.")
    @Test
    void canNotChangeProductPriceWithEmptyPrice() {
        //given
        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setPrice(BigDecimal.valueOf(17000));
        product1.setName("간장치킨");
        productRepository.save(product1);

        Product productWithNullPrice = new Product();
        productWithNullPrice.setPrice(BigDecimal.valueOf(-1));

        //when

        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()->productService.changePrice(product1.getId(),productWithNullPrice));
    }

    @DisplayName("상품의 가격을 변경시, 변경할 가격이 0원 보다 작으면 NoSuchElementException 예외를 발생한다.")
    @Test
    void canNotChangeProductPriceIfProductIsNotBelongToProduct() {
        //given
        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setPrice(BigDecimal.valueOf(17000));
        product1.setName("간장치킨");
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setPrice(BigDecimal.valueOf(16000));

        //when

        //then
        Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(()->productService.changePrice(UUID.randomUUID(),product2));
    }
    @DisplayName("상품 가격 변경 시 메뉴 가격이 전체 상품 가격보다 크면 판매 불가능으로 변경한다.")
    @Test
    void changeProductPriceShouldDisableExpensiveMenu() {
        // given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("한마리메뉴");
        menuGroupRepository.save(menuGroup);

        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setPrice(BigDecimal.valueOf(17000));
        product1.setName("간장치킨");
        productRepository.save(product1);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product1);
        menuProduct.setProductId(product1.getId());
        menuProduct.setQuantity(1);

        List<MenuProduct> menuProducts = List.of(menuProduct);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("간장치킨");
        menu.setPrice(BigDecimal.valueOf(18000));
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(true);
        menu = menuRepository.save(menu);

        Product changedProduct = new Product();
        changedProduct.setPrice(BigDecimal.valueOf(16000));

        // when
        productService.changePrice(product1.getId(), changedProduct);

        // then
        Menu result = menuRepository.findById(menu.getId()).orElseThrow();
        Assertions.assertThat(result.isDisplayed()).isFalse();
    }


    @DisplayName("모든 상품을 조회 할 수 있다.")
    @Test
    void findAll() {

        //given
        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setPrice(BigDecimal.valueOf(17000));
        product1.setName("간장치킨");

        Product product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setPrice(BigDecimal.valueOf(17000));
        product2.setName("순살치킨");

        productRepository.save(product1);
        productRepository.save(product2);

        //when
        List<Product> result = productService.findAll();

        //then
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("간장치킨", "순살치킨");

        BigDecimal totalPrice = result.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Assertions.assertThat(totalPrice).isEqualByComparingTo(BigDecimal.valueOf(34000));

    }
}