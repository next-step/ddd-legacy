package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
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
	private ProductService productService;


	@DisplayName("상품 전체를 조회할 수 있다")
	@Test
	void find_all_product() {
		// given
		Product product1 = new Product("상품 1", new BigDecimal(6000));
		Product product2 = new Product("상품 2", new BigDecimal(7000));
		when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

		// when
		List<Product> products = productService.findAll();

		// then
		Assertions.assertThat(products.size()).isEqualTo(2);
		Assertions.assertThat(products.get(0).getName()).isEqualTo("상품 1");
		Assertions.assertThat(products.get(1).getName()).isEqualTo("상품 2");
	}

	@DisplayName("변경된 가격으로 인해 메뉴상품 비용(가격 X 수량)이 메뉴 가격보다 낮아지면 메뉴를 감춘다")
	@Test
	void hide_condition() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		Product productRequest = new Product("상품", new BigDecimal(15000));
		Product resultProduct = new Product("상품", new BigDecimal(10000));
		when(productRepository.findById(any())).thenReturn(Optional.of(resultProduct));
		MenuGroup menuGroup = new MenuGroup();
		Product product1 = new Product("기존 상품 1", new BigDecimal(6000));
		MenuProduct menuProduct1 = new MenuProduct(product1, 2L);
		MenuProduct menuProduct2 = new MenuProduct(resultProduct, 3L);
		Product product2 = new Product("기존 상품 2", new BigDecimal(8000));
		MenuProduct menuProduct3 = new MenuProduct(product2, 2L);
		MenuProduct menuProduct4 = new MenuProduct(resultProduct, 3L);
		Menu menu1 = new Menu("메뉴 1", new BigDecimal(20000), menuGroup, true, Arrays.asList(menuProduct1, menuProduct2));
		Menu menu2 = new Menu("메뉴 2", new BigDecimal(100000), menuGroup, true, Arrays.asList(menuProduct3, menuProduct4));
		when(menuRepository.findAllByProductId(any())).thenReturn(Arrays.asList(menu1, menu2));

		// when & then
		Product result = productService.changePrice(uuid, productRequest);

		// then
		assertThat(menu2.isDisplayed()).isFalse();
	}

	@DisplayName("변경하려는 상품은 미리 등록되어 있어야 한다")
	@Test
	void target_product_must_have_been_created() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		Product productRequest = new Product("상품", new BigDecimal(15000));
		when(productRepository.findById(any())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productService.changePrice(uuid, productRequest)).isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("변경하려는 상품가격은 필수이고, 0 이상이다")
	@Test
	void target_price_must_be_over_0() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		Product productRequest = new Product("상품", new BigDecimal(-1));

		// when & then
		assertThatThrownBy(() -> productService.changePrice(uuid, productRequest)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("상품 가격을 변경할 수 있다")
	@Test
	void change_price() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		Product productRequest = new Product("상품", new BigDecimal(15000));
		Product resultProduct = new Product("상품", new BigDecimal(10000));
		when(productRepository.findById(any())).thenReturn(Optional.of(resultProduct));
		MenuGroup menuGroup = new MenuGroup();
		Product product1 = new Product("기존 상품 1", new BigDecimal(6000));
		MenuProduct menuProduct1 = new MenuProduct(product1, 2L);
		MenuProduct menuProduct2 = new MenuProduct(resultProduct, 3L);
		Product product2 = new Product("기존 상품 2", new BigDecimal(8000));
		MenuProduct menuProduct3 = new MenuProduct(product2, 2L);
		MenuProduct menuProduct4 = new MenuProduct(resultProduct, 3L);
		Menu menu1 = new Menu("메뉴 1", new BigDecimal(20000), menuGroup, true, Arrays.asList(menuProduct1, menuProduct2));
		Menu menu2 = new Menu("메뉴 2", new BigDecimal(30000), menuGroup, true, Arrays.asList(menuProduct3, menuProduct4));
		when(menuRepository.findAllByProductId(any())).thenReturn(Arrays.asList(menu1, menu2));

		// when
		Product result = productService.changePrice(uuid, productRequest);

		// then
		assertThat(result.getPrice()).isEqualTo(new BigDecimal(15000));
	}

	@DisplayName("상품 이름은 필수이고, 상품 이름은 욕설을 포함할 수 없다")
	@Test
	void name_can_not_contrain_purgomalum() {
		// given
		Product requestWithNoInfo = new Product();
		Product requestWithPurgomalum = new Product("비속어", new BigDecimal(10000));
		when(purgomalumClient.containsProfanity("비속어")).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> productService.create(requestWithNoInfo)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> productService.create(requestWithPurgomalum)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("상품 가격은 필수이고, 0 이상이다")
	@Test
	void price_must_be_over_0() {
		// given
		Product requestWithNoInfo = new Product();
		Product requestWithNegative = new Product("상품", new BigDecimal(-1));

		// when & then
		assertThatThrownBy(() -> productService.create(requestWithNoInfo)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> productService.create(requestWithNegative)).isInstanceOf(IllegalArgumentException.class);;
	}

	@DisplayName("상품을 만들 수 있다")
	@Test
	void create_prodcut() {
		// given
		when(purgomalumClient.containsProfanity(anyString())).thenReturn(false);
		Product result = new Product("상품", new BigDecimal(10000));
		when(productRepository.save(any())).thenReturn(result);
		Product request = new Product("상품", new BigDecimal(10000));

		// when
		Product product = productService.create(request);

		// then
		assertThat(product.getName()).isEqualTo("상품");
		assertThat(product.getPrice()).isEqualTo(new BigDecimal(10000));
	}
}
