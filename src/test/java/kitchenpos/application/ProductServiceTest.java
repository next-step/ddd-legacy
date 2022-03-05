package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static kitchenpos.stub.MenuStub.generateSingleSizeInValidPriceTestMenus;
import static kitchenpos.stub.MenuStub.generateSingleSizeValidPriceTestMenus;
import static kitchenpos.stub.ProductStub.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {

    @Mock
    private PurgomalumClient purgomalumClient;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private ProductService productService;


    @DisplayName("새 상품을 등록할 수 있다.")
    @Test
    void createNewProduct() {
        //given
        Product newProduct = generateThousandPriceProduct();
        when(purgomalumClient.containsProfanity(any())).thenReturn(false);
        when(productRepository.save(any())).thenReturn(newProduct);

        //when
        Product result = productService.create(newProduct);

        //then
        assertThat(result).isEqualTo(newProduct);
    }

    @DisplayName("상품 이름은 비속어를 사용할 수 없다.")
    @Test
    void notAllowProfanity() {
        //given
        Product newProduct = generateThousandPriceProduct();
        when(purgomalumClient.containsProfanity(any())).thenReturn(true);

        //when & then
        assertThatThrownBy(() -> productService.create(newProduct)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격은 0원 이상이어야 한다.")
    @Test
    void mustBePositivePrice() {
        //given
        Product negativePriceProduct = generateNegativePriceProduct();

        //when & then
        assertThatThrownBy(() -> productService.create(negativePriceProduct)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이미 등록 된 상품의 가격을 변경할 수 있다.")
    @Test
    void canChangePrice() {
        //given
        Product createdProduct = generateThousandPriceProduct();
        Product priceChangedProduct = generateTwoThousandPriceProduct();
        List<Menu> createdProductRelatedMenus = generateSingleSizeValidPriceTestMenus();
        when(productRepository.findById(any())).thenReturn(Optional.of(createdProduct));
        when(menuRepository.findAllByProductId(any())).thenReturn(createdProductRelatedMenus);

        //when
        Product result = productService.changePrice(createdProduct.getId(), priceChangedProduct);

        //then
        assertThat(result.getPrice()).isEqualTo(priceChangedProduct.getPrice());
    }

    @DisplayName("이미 등록 된 상품의 가격을 음수로는 변경할 수 없다.")
    @Test
    void canNotChangeNegativePrice() {
        //given
        Product createdProduct = generateThousandPriceProduct();
        Product negativePriceProduct = generateNegativePriceProduct();

        //when & then
        assertThatThrownBy(() -> productService.changePrice(createdProduct.getId(), negativePriceProduct)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록되지 않은 상품의 가격은 변경할 수 없다.")
    @Test
    void canNotChangeNoneCreatedProduct() {
        //given
        Product createdProduct = generateThousandPriceProduct();
        Product priceChangedProduct = generateTwoThousandPriceProduct();
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> productService.changePrice(createdProduct.getId(), priceChangedProduct)).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("상품의 가격을 변경할 때 상품이 포함된 메뉴에 속한 메뉴상품들의 가격합을 재계산해서 메뉴의 가격이 가격합보다 크면 메뉴를 숨긴다.")
    @Test
    void hideMenuWhenMenuPriceBiggerThanSumOfMenuProducts() {
        //given
        Product createdProduct = generateThousandPriceProduct();
        Product priceChangedProduct = generateTwoThousandPriceProduct();
        List<Menu> createdProductRelatedMenus = generateSingleSizeInValidPriceTestMenus();
        when(productRepository.findById(any())).thenReturn(Optional.of(createdProduct));
        when(menuRepository.findAllByProductId(any())).thenReturn(createdProductRelatedMenus);

        //when
        productService.changePrice(createdProduct.getId(), priceChangedProduct);

        //then
        Menu invalidPriceMenu = createdProductRelatedMenus.get(0);
        assertThat(invalidPriceMenu.isDisplayed()).isFalse();
    }

    @DisplayName("상품 전체를 조회할 수 있다.")
    @Test
    void canFindAllProducts() {
        //given
        List<Product> allProducts = new ArrayList<>();
        allProducts.add(generateThousandPriceProduct());
        allProducts.add(generateTwoThousandPriceProduct());
        when(productRepository.findAll()).thenReturn(allProducts);

        //when
        List<Product> results = productService.findAll();

        assertThat(results).containsExactlyElementsOf(allProducts);
    }
}
