package kitchenpos.unit;

import kitchenpos.application.ProductService;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductTest extends UnitTestRunner {

    @InjectMocks
    private ProductService productService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @DisplayName("제품 생성")
    @Test
    public void create() {
        //given
        final String productName = "후라이드 치킨";
        final BigDecimal productPrice = BigDecimal.valueOf(10000);

        final Product request = new Product();
        request.setName(productName);
        request.setPrice(productPrice);

        final Product stubbedProduct = new Product();
        stubbedProduct.setId(UUID.randomUUID());
        stubbedProduct.setPrice(productPrice);
        stubbedProduct.setName(productName);

        when(productRepository.save(any(Product.class))).thenReturn(stubbedProduct);

        //when
        final Product product = productService.create(request);

        //then
        assertThat(product.getId()).isNotNull();
        assertThat(product.getName()).isEqualTo(productName);
        assertThat(product.getPrice()).isEqualTo(productPrice);
        verify(purgomalumClient, times(1)).containsProfanity(productName);
    }

    @DisplayName("제품 가격 변경")
    @Test
    public void changePrice() {
        //given
        final String productName = "후라이드 치킨";
        final BigDecimal productPrice = BigDecimal.valueOf(10000);

        final Product request = new Product();
        final BigDecimal changePrice = BigDecimal.valueOf(9000);
        request.setName(productName);
        request.setPrice(changePrice);

        final Product stubbedProduct = new Product();
        final UUID productId = UUID.randomUUID();
        stubbedProduct.setId(productId);
        stubbedProduct.setPrice(productPrice);
        stubbedProduct.setName(productName);

        final MenuProduct stubbedMenuProduct = new MenuProduct();
        stubbedMenuProduct.setProduct(stubbedProduct);

        final Menu stubbedMenu = new Menu();
        stubbedMenu.setPrice(BigDecimal.valueOf(9000));
        stubbedMenu.setMenuProducts(List.of(stubbedMenuProduct));

        when(productRepository.findById(productId)).thenReturn(Optional.of(stubbedProduct));
        when(menuRepository.findAllByProductId(productId)).thenReturn(List.of(stubbedMenu));

        //when
        final Product changePriceProduct = productService.changePrice(productId, request);

        //then
        assertThat(changePriceProduct.getPrice()).isEqualTo(changePrice);
    }

    @DisplayName("모든 제품 조회")
    @Test
    public void findAll() {
        //given
        final String productName_1 = "후라이드 치킨";
        final BigDecimal productPrice_1 = BigDecimal.valueOf(10000);

        final Product stubbedProduct_1 = new Product();
        stubbedProduct_1.setId(UUID.randomUUID());
        stubbedProduct_1.setPrice(productPrice_1);
        stubbedProduct_1.setName(productName_1);

        final String productName_2 = "양념 치킨";
        final BigDecimal productPrice_2 = BigDecimal.valueOf(10000);

        final Product stubbedProduct_2 = new Product();
        stubbedProduct_2.setId(UUID.randomUUID());
        stubbedProduct_2.setPrice(productPrice_2);
        stubbedProduct_2.setName(productName_2);

        final String productName_3 = "간장 치킨";
        final BigDecimal productPrice_3 = BigDecimal.valueOf(10000);

        final Product stubbedProduct_3 = new Product();
        stubbedProduct_3.setId(UUID.randomUUID());
        stubbedProduct_3.setPrice(productPrice_3);
        stubbedProduct_3.setName(productName_3);

        when(productRepository.findAll()).thenReturn(List.of(stubbedProduct_1, stubbedProduct_2, stubbedProduct_3));

        //when
        final List<Product> products = productService.findAll();

        //then
        assertAll(
                () -> assertThat(products.size()).isEqualTo(3),
                () -> assertThat(products.get(0)).isEqualTo(stubbedProduct_1),
                () -> assertThat(products.get(1)).isEqualTo(stubbedProduct_2),
                () -> assertThat(products.get(2)).isEqualTo(stubbedProduct_3)
        );
    }
}
