package kitchenpos.application;

import static kitchenpos.application.MenuServiceFixture.menu;
import static kitchenpos.application.MenuServiceFixture.menus;
import static kitchenpos.application.ProductServiceFixture.product;
import static kitchenpos.application.ProductServiceFixture.products;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    ProductService productService;

    private static final String FUCK = "fuck";

    @DisplayName("상품을 등록할 수 있다.")
    @Test
    void create() {

        //given
        Product product = product();

        //when
        productService.create(product);

        //then
        verify(productRepository).save(any());
    }

    @DisplayName("상품 가격은 비어있을 수 없다.")
    @Test
    void can_not_be_empty_productPrice() {

        //given
        Product product = product();
        product.setPrice(null);

        //when

        //then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품 가격은 0원 이상 이어야 한다.")
    @Test
    void must_be_more_than_zero_productPrice() {

        //given
        BigDecimal productPrice = BigDecimal.valueOf(-1);
        Product product = product();
        product.setPrice(productPrice);

        //when

        //then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품 이름은 비어 있을 수 없다.")
    @Test
    void can_not_be_empty_productName() {

        //given
        Product product = product();
        product.setName(null);

        //when

        //then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품 이름은 통상적인 비속어는 포함될 수 없다.")
    @Test
    void can_not_contain_profanity_productName() {

        //given
        given(purgomalumClient.containsProfanity(FUCK)).willReturn(true);
        Product product = product();
        product.setName(FUCK);

        //when

        //then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품의 가격을 변경할 수 있다.")
    @Test
    void changePrice() {

        //given
        Product product = product();
        BigDecimal originPrice = product.getPrice();
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        given(menuRepository.findAllByProductId(product.getId())).willReturn(menus());
        product.setPrice(BigDecimal.valueOf(19_000));

        //when
        Product result = productService.changePrice(product.getId(), product);

        //then
        assertAll(
            () -> verify(productRepository).findById(product.getId()),
            () -> verify(menuRepository).findAllByProductId(product.getId()),
            () -> assertThat(result.getPrice()).isNotEqualTo(originPrice)
        );
    }

    @DisplayName("메뉴들의 가격이 각 메뉴 상품의 합보다 큰 경우 메뉴는 전시될 수 없다.")
    @Test
    void can_not_be_displayed_menus_greater_than_sum_of_each_menuProduct() {

        Product product = product();
        BigDecimal originPrice = product.getPrice();

        List<Menu> menus = menus();
        Menu menu = menu();
        menu.setDisplayed(true);
        menus.add(menu);

        product.setPrice(BigDecimal.valueOf(17_000));
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        given(menuRepository.findAllByProductId(product.getId())).willReturn(menus);

        //when
        Product result = productService.changePrice(product.getId(), product);

        //then
        assertAll(
            () -> verify(productRepository).findById(product.getId()),
            () -> verify(menuRepository).findAllByProductId(product.getId()),
            () -> assertThat(result.getPrice()).isNotEqualTo(originPrice),
            () -> assertThat(menu.isDisplayed()).isTrue()
        );
    }

    @DisplayName("상품의 목록을 조회할 수 있다.")
    @Test
    void findAll() {

        //given
        List<Product> products = products();
        given(productRepository.findAll()).willReturn(products);

        //when
        List<Product> result = productService.findAll();

        //then
        assertThat(result).hasSize(products.size());
    }

}