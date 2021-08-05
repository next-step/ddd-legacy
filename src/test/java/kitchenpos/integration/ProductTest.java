package kitchenpos.integration;

import kitchenpos.application.ProductService;
import kitchenpos.domain.*;
import kitchenpos.integration.annotation.TestAndRollback;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ProductTest extends IntegrationTestRunner {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuRepository menuRepository;

    @DisplayName("제품 생성 ( 제품 가격은 'null' 이 될 수 없다. )")
    @TestAndRollback
    public void create_with_null_price() {
        //given
        final Product request = new Product();
        request.setName("후라이드 치킨");

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(request));
    }

    @DisplayName("제품 생성 ( 제품 가격은 '0'보다 작을수 없다. )")
    @TestAndRollback
    public void create_with_minus_price() {
        //given
        final BigDecimal minusPrice = BigDecimal.valueOf(-1);
        Product request = new Product();
        request.setPrice(minusPrice);
        request.setName("후라이드 치킨");

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(request));
    }

    @DisplayName("제품 생성 ( 제품 이름은 'null' 이 될 수 없다.. )")
    @TestAndRollback
    public void create_with_null_name() {
        //given
        final BigDecimal minusPrice = BigDecimal.valueOf(-1);
        Product request = new Product();
        request.setPrice(minusPrice);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(request));
    }

    @DisplayName("제품 생성 ( 제품 이름은 '비속어' 를 포함하면 안된다. )")
    @TestAndRollback
    public void create_with_profanity_name() {
        //given
        final BigDecimal minusPrice = BigDecimal.valueOf(-1);
        final String profanityName = "Bitch";
        Product request = new Product();
        request.setPrice(minusPrice);
        request.setName(profanityName);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(request));
    }

    @DisplayName("제품 생성")
    @TestAndRollback
    public void create() {
        //given
        final BigDecimal price = BigDecimal.valueOf(10000);
        final String productName = "후라이드 치킨";
        Product request = new Product();
        request.setPrice(price);
        request.setName(productName);

        //when
        final Product product = productService.create(request);

        //then
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getName()).isEqualTo(productName);
        assertThat(product.getId()).isNotNull();
    }

    @DisplayName("제품 가격 변경 ( 변경 가격은 'null' 일 수 없다. )")
    @TestAndRollback
    public void changePrice_with_null_price() {
        //given
        final String productName = "후라이드 치킨";
        final UUID productUuid = UUID.randomUUID();
        Product request = new Product();

        final Product product = new Product();
        product.setId(productUuid);
        product.setName(productName);
        product.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.changePrice(productUuid, request));

    }

    @DisplayName("제품 가격 변경 ( 변경 가격은 '0' 보다 작을 수 없다. )")
    @TestAndRollback
    public void changePrice_with_minus_price() {
        //given
        final BigDecimal minusPrice = BigDecimal.valueOf(-1);
        final String productName = "후라이드 치킨";
        final UUID productUuid = UUID.randomUUID();
        Product request = new Product();
        request.setPrice(minusPrice);

        final Product product = new Product();
        product.setId(productUuid);
        product.setName(productName);
        product.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.changePrice(productUuid, request));

    }

    @DisplayName("제품 가격 변경 ( 변경 제품은 영속화 되어 있어야 한다. )")
    @TestAndRollback
    public void changePrice_with_not_persisted_product() {
        //given
        final String productName = "후라이드 치킨";
        final UUID productUuid = UUID.randomUUID();
        Product request = new Product();
        request.setPrice(BigDecimal.valueOf(10000));

        final Product product = new Product();
        product.setId(productUuid);
        product.setName(productName);
        product.setPrice(BigDecimal.valueOf(15000));

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> productService.changePrice(productUuid, request));

    }

    @DisplayName("제품 가격 변경 ( 제품 가격 변경 후 '메뉴의 가격' 은 메뉴에 구성된 '제품 가격의 합' 보다 크다면 메뉴를 숨김처리 한다. )")
    @TestAndRollback
    public void changePrice_with_bigger_menu_price() {
        //given
        final UUID menuUuId = UUID.randomUUID();
        final UUID menuGroupUuId = UUID.randomUUID();
        final String productName = "후라이드 치킨";
        final UUID productUuid = UUID.randomUUID();
        final BigDecimal lowerPrice = BigDecimal.valueOf(12000);

        final Product product = new Product();
        product.setId(productUuid);
        product.setName(productName);
        product.setPrice(BigDecimal.valueOf(15000));

        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupUuId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final Product product_1 = new Product();
        product_1.setId(productUuid);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productUuid);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final Menu menu = new Menu();
        menu.setId(menuUuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(15000));
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        Product request = new Product();
        request.setPrice(lowerPrice);

        //when
        final Product changeProduct = productService.changePrice(productUuid, request);
        final Menu hideMenu = menuRepository.getById(menuUuId);

        //then
        assertThat(changeProduct.getPrice()).isEqualTo(lowerPrice);
        assertThat(hideMenu.isDisplayed()).isFalse();
    }

    @DisplayName("제품 가격 변경")
    @TestAndRollback
    public void changePrice() {
        //given
        final UUID menuUuId = UUID.randomUUID();
        final UUID menuGroupUuId = UUID.randomUUID();
        final String productName = "후라이드 치킨";
        final UUID productUuid = UUID.randomUUID();
        final BigDecimal changePrice = BigDecimal.valueOf(17000);

        final Product product = new Product();
        product.setId(productUuid);
        product.setName(productName);
        product.setPrice(BigDecimal.valueOf(15000));

        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupUuId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final Product product_1 = new Product();
        product_1.setId(productUuid);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productUuid);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final Menu menu = new Menu();
        menu.setId(menuUuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(15000));
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        Product request = new Product();
        request.setPrice(changePrice);

        //when
        final Product changeProduct = productService.changePrice(productUuid, request);
        final Menu hideMenu = menuRepository.getById(menuUuId);

        //then
        assertThat(changeProduct.getPrice()).isEqualTo(changePrice);
        assertThat(hideMenu.isDisplayed()).isTrue();
    }

    @DisplayName("모든 제품 조회")
    @TestAndRollback
    public void findAll() {
        //given
        final Product product_1 = new Product();
        product_1.setId(UUID.randomUUID());
        product_1.setName("양념 치킨");
        product_1.setPrice(BigDecimal.valueOf(16000));

        final Product product_2 = new Product();
        product_2.setId(UUID.randomUUID());
        product_2.setName("후라이드 치킨");
        product_2.setPrice(BigDecimal.valueOf(15000));

        final Product product_3 = new Product();
        product_3.setId(UUID.randomUUID());
        product_3.setName("간장 치킨");
        product_3.setPrice(BigDecimal.valueOf(16000));

        productRepository.saveAll(List.of(product_1, product_2, product_3));

        //when
        final List<Product> products = productService.findAll();

        //then
        assertThat(products.size()).isEqualTo(3);
        assertThat(products.get(0)).isEqualTo(product_1);
        assertThat(products.get(1)).isEqualTo(product_2);
        assertThat(products.get(2)).isEqualTo(product_3);
    }

}
