package kitchenpos.product;

import kitchenpos.application.ProductService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @DisplayName("상품을 생성한다.")
    @Test
    void create() {
        Product product = new Product("치킨", BigDecimal.valueOf(20000));
        when(purgomalumClient.containsProfanity(any())).thenReturn(false);
        when(productRepository.save(any())).thenReturn(product);

        Product 치킨 = productService.create(product);

        assertAll(
                () -> assertThat(치킨.getName()).isEqualTo("치킨"),
                () -> assertThat(치킨.getPrice()).isEqualTo(BigDecimal.valueOf(20000))
        );
    }

    @DisplayName("이름이 없는 상품을 생성할 수 없다.")
    @Test
    void createWithNullName() {
        Product product = new Product(null, BigDecimal.valueOf(20000));

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격을 변경한다.")
    @Test
    void changePrice() {
        Product product = new Product(UUID.randomUUID(), "치킨", BigDecimal.valueOf(20000));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(menuRepository.findAllByProductId(any())).thenReturn(Collections.emptyList());

        Product changedProduct = productService.changePrice(product.getId(), new Product(null, BigDecimal.valueOf(15000)));

        assertThat(changedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(15000));
    }

    @DisplayName("상품의 가격을 변경할 때, 해당 상품을 포함하는 메뉴의 가격이 메뉴상품의 가격보다 작아지면 해당 메뉴는 숨겨진다.")
    @Test
    void changePriceWithMenuHide() {
        Product 후라이드 = new Product(UUID.randomUUID(), "후라이드", BigDecimal.valueOf(20000));
        Menu 두마리메뉴 = new Menu("두마리메뉴", BigDecimal.valueOf(30000), true, List.of(new MenuProduct(후라이드, 2, 후라이드.getId())), null);
        when(productRepository.findById(후라이드.getId())).thenReturn(Optional.of(후라이드));
        when(menuRepository.findAllByProductId(any())).thenReturn(List.of(두마리메뉴));

        Product 변경된_상품 = productService.changePrice(후라이드.getId(), new Product(null, BigDecimal.valueOf(14000)));

        assertAll(
                () -> assertThat(변경된_상품.getPrice()).isEqualTo(BigDecimal.valueOf(14000)),
                () -> assertThat(두마리메뉴.isDisplayed()).isFalse()
        );
    }

    @DisplayName("상품 목록을 조회한다.")
    @Test
    void findAll() {
        Product 후라이드_치킨 = new Product("후라이드 치킨", BigDecimal.valueOf(20000));
        Product 간장_치킨 = new Product("간장 치킨", BigDecimal.valueOf(22000));
        Product 양념_치킨 = new Product("양념 치킨", BigDecimal.valueOf(23000));
        when(productRepository.findAll()).thenReturn(List.of(후라이드_치킨, 간장_치킨, 양념_치킨));

        List<String> 상품목록 = productService.findAll().stream()
                .map(Product::getName)
                .collect(Collectors.toList());

        assertThat(상품목록).containsExactly("후라이드 치킨", "간장 치킨", "양념 치킨");
    }
}
