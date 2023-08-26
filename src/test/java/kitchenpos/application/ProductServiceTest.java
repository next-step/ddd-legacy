package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.TestFixture.*;
import static kitchenpos.fixture.TestFixture.TEST_MENU_PRODUCT;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    @Test
    @DisplayName("새로운 상품을 등록한다.")
    void createTest() {
        // given
        Product createRequest = TEST_PRODUCT();
        given(purgomalumClient.containsProfanity(anyString())).willReturn(false);
        given(productRepository.save(any(Product.class))).willReturn(createRequest);

        // when
        productService.create(createRequest);

        // then
        verify(purgomalumClient, times(1)).containsProfanity(anyString());
        verify(productRepository, times(1)).save(any());
    }

    @DisplayName("이름은 비어있을 수 없다.")
    @Test
    void createNameEmptyTest() {
        // given
        Product nameTest1 = TEST_PRODUCT();
        nameTest1.setName(null);
        Product nameTest2 = TEST_PRODUCT();
        nameTest2.setName(" ");
        Product nameTest3 = TEST_PRODUCT();
        nameTest3.setName("");

        // when && then
        assertThatThrownBy(() -> productService.create(nameTest1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatNoException().isThrownBy(() -> productService.create(nameTest2));
        assertThatNoException().isThrownBy(() -> productService.create(nameTest3));
    }

    @DisplayName("이름은 외설적이거나 욕설이 포함된 영어 이름은 사용 할 수 없다")
    @ParameterizedTest
    @CsvSource(value = {"fuck", "shit"})
    void createNameTest(String name) {
        // given
        Product createRequest = TEST_PRODUCT();
        createRequest.setName(name);
        given(purgomalumClient.containsProfanity(name)).willReturn(true);

        // when && then
        assertThatThrownBy(() -> productService.create(createRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품의 가격을 변경한다")
    void changePriceTest() {
        // given
        Product product = TEST_PRODUCT();
        Product changeProduct = TEST_PRODUCT();
        changeProduct.setPrice(new BigDecimal(1500));
        UUID productId = product.getId();
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        Menu menu = TEST_MENU();
        MenuProduct menuProduct = TEST_MENU_PRODUCT();
        menuProduct.setProduct(changeProduct);
        menuProduct.setProductId(changeProduct.getId());
        menuProduct.setQuantity(2);
        menu.setMenuProducts(List.of(menuProduct));
        given(menuRepository.findAllByProductId(productId)).willReturn(List.of(menu));

        Product result = productService.changePrice(productId, changeProduct);

        // then
        verify(productRepository, times(1)).findById(productId);
        verify(menuRepository, times(1)).findAllByProductId(productId);
        List<Menu> allByProductId = menuRepository.findAllByProductId(productId);

        assertThat(result.getPrice()).isEqualTo(changeProduct.getPrice());
        assertThat(allByProductId).extracting("displayed").containsExactly(true);
    }

    @Test
    @DisplayName("가격 변경 후, 메뉴의 가격이 (메뉴에 포함된 상품들의 가격 x 개수) 총 합보다 높다면 메뉴를 비활성화 한다.")
    void changePriceAndHideTest() {
        // given
        Product product = TEST_PRODUCT();
        Product changeProduct = TEST_PRODUCT();
        changeProduct.setPrice(new BigDecimal(10));
        UUID productId = product.getId();
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        Menu menu = TEST_MENU();
        MenuProduct menuProduct = TEST_MENU_PRODUCT();
        menuProduct.setProduct(changeProduct);
        menuProduct.setProductId(changeProduct.getId());
        menuProduct.setQuantity(2);
        menu.setMenuProducts(List.of(menuProduct));
        given(menuRepository.findAllByProductId(productId)).willReturn(List.of(menu));

        Product result = productService.changePrice(productId, changeProduct);

        // then
        verify(productRepository, times(1)).findById(productId);
        verify(menuRepository, times(1)).findAllByProductId(productId);
        List<Menu> allByProductId = menuRepository.findAllByProductId(productId);

        assertThat(result.getPrice()).isEqualTo(changeProduct.getPrice());
        assertThat(allByProductId).extracting("displayed").containsExactly(false);
    }
}