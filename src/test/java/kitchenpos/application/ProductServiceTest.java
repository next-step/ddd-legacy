package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kitchenpos.objectmother.ProductMaker.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ProductRepository productRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

    @Autowired
    private ProductService productService;

    @DisplayName("상품생성 후 상품조회시 추가한 상품이 조회되야 한다.")
    @Test
    void 상품생성() {
        // when
        Product saveProduct = productService.create(상품_1);

        // then
        Product findProduct = productRepository.findById(saveProduct.getId()).orElse(null);
        assertThat(findProduct).isNotNull();
        assertThat(findProduct.getName()).isEqualTo(상품_1.getName());
        assertThat(findProduct.getPrice()).isEqualTo(상품_1.getPrice());
    }

    @DisplayName("상품생성 시 가격이 0원보다 작을경우 에러를 던진다.")
    @Test
    void 상품생성실패_가격음수() {
        // when then
        assertThatThrownBy(() -> productService.create(음수가격상품))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품생성 시 가격이름에 욕설이 포함되있을경우 에러를 던진다.")
    @Test
    void 상품생성실패_가격이름_욕설포함() {
        // given
        when(purgomalumClient.containsProfanity(any())).thenReturn(true);

        // when then
        assertThatThrownBy(() -> productService.create(욕설상품))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품가격변경시 해당 상품조회시 변경된 가격이 조회되야 한다.")
    @Test
    void 상품가격변경() {
        // given
        Product saveProduct = productService.create(상품_1);

        // when
        productService.changePrice(saveProduct.getId(), 상품_2);

        // then
        Product findProduct = productRepository.findById(saveProduct.getId()).orElse(null);
        assertThat(findProduct.getPrice()).isEqualTo(상품_2.getPrice());
    }

    @DisplayName("상품가격변경시 해당 상품으로 구성된 메뉴가격이 메뉴상품 총 가격을 " +
            "초과할 경우 해당 메뉴를 비활성화 해야 한다.")
    @Test
    void 상품가격변경_메뉴가격_메뉴상품총가격_초과() {

    }

    @DisplayName("상품 전체조회시 지금까지 등록된 상품이 전부 조회되야 한다.")
    @Test
    void 상품전체조회() {
        // given
        productService.create(상품_1);
        productService.create(상품_2);

        // when
        List<Product> products = productService.findAll();

        // then
        assertThat(products)
                .hasSize(2)
                .extracting(Product::getName, Product::getPrice)
                .containsExactlyInAnyOrder(
                        Tuple.tuple(상품_1.getName(), 상품_1.getPrice()),
                        Tuple.tuple(상품_2.getName(), 상품_2.getPrice())
                );
    }
}