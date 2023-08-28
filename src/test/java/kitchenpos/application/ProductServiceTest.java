package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    ProductService productService;

    @Mock
    ProductRepository productRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    PurgomalumClient purgomalumClient;
    @BeforeEach
    void setup() {
        this.productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("신규 제품 생성 시 가격이 입력되어야하며, 음수가 아니어야한다.")
    @Test
    void createPrice() {
        Product product = createProduct(UUID.randomUUID(),"Testprice", new BigDecimal(-1));

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("제품명은 입력되어야하며, 비속어가 없어야한다.")
    @Test
    void createName() {
        Product product = createProduct(UUID.randomUUID(),null, new BigDecimal(100));
        //when(purgomalumClient.containsProfanity(any())).thenReturn(true);
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);


    }

    @DisplayName("가격 변경시 변경 가격이 입력되어야하며, 음수가 아니어야한다.")
    @Test
    void changePrice() {
        Product product1 = createProduct(UUID.randomUUID(),"test1", new BigDecimal(100));
        //when(purgomalumClient.containsProfanity(any())).thenReturn(true);
        Product product2 = createProduct(UUID.randomUUID(),"test2", new BigDecimal(-1));

        assertThatThrownBy(() -> productService.changePrice(product1.getId(),product2))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("상품가격*수량을 한 합이 메뉴 가격보다 작은 경우 메뉴를 숨긴다.")
    @Test
    void hide() {
        Product product1 = createProduct(UUID.randomUUID(),"test1", new BigDecimal(100));
        //when(purgomalumClient.containsProfanity(any())).thenReturn(true);
        Product product2 = createProduct(UUID.randomUUID(),"test2", new BigDecimal(1000));
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product1);
        menuProduct.setQuantity(1L);

        Menu menu = new Menu();
        menu.setPrice(new BigDecimal(10000));
        menu.setMenuProducts(List.of(menuProduct));
        menu.setDisplayed(true);

        when(productRepository.findById(any())).thenReturn(Optional.of(product2));
        when(menuRepository.findAllByProductId(any())).thenReturn(List.of(menu));

        Product productResult = productService.changePrice(product1.getId(),product2);
        assertThat(menu.isDisplayed()).isFalse();

    }


    public Product createProduct(UUID id, String name, BigDecimal price){
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

}
