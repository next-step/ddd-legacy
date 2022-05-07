package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
	@Mock
	private MenuRepository menuRepository;

	@Mock
	private MenuGroupRepository menuGroupRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private PurgomalumClient purgomalumClient;

	@InjectMocks
	private MenuService menuService;

	@DisplayName("메뉴상품의 수량은 0 이상이어야 한다")
	@Test
	void quantity_of_menu_product_must_be_over_zero() {
		// given
		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, -1L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(20000), menuGroup, true, Collections.singletonList(menuProduct));

		when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
		when(productRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(product));

		// when & then
		assertThatThrownBy(() -> menuService.create(menuRequest));
	}

	@DisplayName("메뉴상품은 상품을 한개 가지고 있다")
	@Test
	void menu_product_has_one_product() {
		// given
		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
		when(productRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(product));
		when(productRepository.findById(any())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> menuService.create(menuRequest)).isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("메뉴 상품은 반드시 상품이 먼저 등록되어 있어야 한다")
	@Test
	void menu_product_must_have_a_product_that_is_registered_in_advance() {
		// given
		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
		when(productRepository.findAllByIdIn(any())).thenReturn(Collections.emptyList());

		// when & then
		assertThatThrownBy(() -> menuService.create(menuRequest)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("메뉴 전체를 조회할 수 있다")
	@Test
	void find_all_menu() {
		// given
		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(20000), menuGroup, true, Collections.singletonList(menuProduct));

		when(menuRepository.findAll()).thenReturn(Collections.singletonList(menu));

		// when
		List<Menu> result = menuService.findAll();

		// then
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.get(0).getName()).isEqualTo("메뉴");
		assertThat(result.get(0).getPrice()).isEqualTo(new BigDecimal(20000));
		assertThat(result.get(0).isDisplayed()).isTrue();
	}

	@DisplayName("감추려는 메뉴는 미리 등록되어 있어야 한다")
	@Test
	void when_to_hide_the_menu_must_exist() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		when(menuRepository.findById(uuid)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> menuService.hide(uuid)).isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("메뉴를 감출 수 있다")
	@Test
	void hide_menu() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(20000), menuGroup, true, Collections.singletonList(menuProduct));

		when(menuRepository.findById(uuid)).thenReturn(Optional.of(menu));

		// when
		Menu result = menuService.hide(uuid);

		// then
		assertThat(result.isDisplayed()).isFalse();
	}

	@DisplayName("메뉴 가격은 각 메뉴 상품 비용보다 같거나 작아야 한다")
	@Test
	void when_to_display_menu_product_cost_is_bigger_than_menu_price() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(1000000), menuGroup, false, Collections.singletonList(menuProduct));

		when(menuRepository.findById(uuid)).thenReturn(Optional.of(menu));

		// when & then
		assertThatThrownBy(() -> menuService.display(uuid)).isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("노출하려는 메뉴는 미리 등록되어 있어야한다")
	@Test
	void when_to_display_the_menu_must_exist() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		when(menuRepository.findById(uuid)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> menuService.display(uuid)).isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("메뉴를 노출 시킬 수 있다")
	@Test
	void display_menu() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, false, Collections.singletonList(menuProduct));

		when(menuRepository.findById(uuid)).thenReturn(Optional.of(menu));
		// when
		Menu result = menuService.display(uuid);

		// then
		assertThat(result.isDisplayed()).isTrue();
	}

	@DisplayName("변경하려는 메뉴 가격은 각 메뉴상품 비용보다 같거나 작아야 한다")
	@Test
	void when_to_change_menu_product_cost_is_bigger_than_menu_price() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		Menu menuRequest = new Menu();
		menuRequest.setPrice(new BigDecimal(15000));

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menu = new Menu("메뉴", new BigDecimal(100000), menuGroup, true, Collections.singletonList(menuProduct));

		when(menuRepository.findById(uuid)).thenReturn(Optional.of(menu));
		// when & then
		assertThatThrownBy(() -> menuService.changePrice(uuid, menuRequest)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("변경하려는 메뉴는 미리 등록되어 있어야 한다")
	@Test
	void when_to_change_menu_must_exist() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		Menu menuRequest = new Menu();
		menuRequest.setPrice(new BigDecimal(15000));

		when(menuRepository.findById(uuid)).thenReturn(Optional.empty());
		// when & then
		assertThatThrownBy(() -> menuService.changePrice(uuid, menuRequest)).isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("변경하려는 메뉴 가격은 필수이며, 0원 이상이어야 한다")
	@Test
	void when_to_change_menu_price_is_must_and_is_over_zero() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		Menu menuRequest = new Menu();
		menuRequest.setPrice(new BigDecimal(-1));

		// when & then
		assertThatThrownBy(() -> menuService.changePrice(uuid, menuRequest)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("메뉴의 가격을 변경할 수 있다")
	@Test
	void change_menu_price() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		Menu menuRequest = new Menu();
		menuRequest.setPrice(new BigDecimal(15000));

		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 3L);
		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		when(menuRepository.findById(uuid)).thenReturn(Optional.of(menu));
		// when
		Menu result = menuService.changePrice(uuid, menuRequest);

		// then
		assertThat(result.getPrice()).isEqualTo(new BigDecimal(15000));
	}

	@DisplayName("메뉴의 이름은 비속어 등을 포함할 수 없다")
	@Test
	void menu_name_can_not_contain_purgomalum() {
		// given
		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(10000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(20000), menuGroup, true, Collections.singletonList(menuProduct));

		when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
		when(productRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(product));
		when(productRepository.findById(any())).thenReturn(Optional.of(product));
		when(purgomalumClient.containsProfanity(anyString())).thenReturn(true);
		// when & then
		assertThatThrownBy(() -> menuService.create(menuRequest)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("메뉴 상품 비용의 총합이 메뉴 가격보다 같거나 더 커야한다")
	@Test
	void menu_product_cost_sum_is_bigger_or_equal_to_menu_price() {
		// given
		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(5000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(20000), menuGroup, true, Collections.singletonList(menuProduct));

		when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
		when(productRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(product));
		when(productRepository.findById(any())).thenReturn(Optional.of(product));

		// when & then
		assertThatThrownBy(() -> menuService.create(menuRequest)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("메뉴상품은 반드시 한개 이상 포함되어야 한다")
	@Test
	void menu_product_must_be_contained() {
		// given
		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(10000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
		when(productRepository.findAllByIdIn(any())).thenReturn(Collections.emptyList());

		// when & then
		assertThatThrownBy(() -> menuService.create(menuRequest)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("메뉴는 반드시 메뉴그룹을 가져야 한다")
	@Test
	void menu_must_have_menu_group() {
		// given
		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(10000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(20000), menuGroup, true, Collections.singletonList(menuProduct));

		when(menuGroupRepository.findById(any())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> menuService.create(menuRequest)).isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("메뉴 가격은 필수이며, 0원 이상이어야 한다")
	@Test
	void menu_price_is_must_and_is_over_zero() {
		// given
		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(-1));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(20000), menuGroup, true, Collections.singletonList(menuProduct));

		when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
		when(productRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(product));
		when(productRepository.findById(any())).thenReturn(Optional.of(product));

		// when & then
		assertThatThrownBy(() -> menuService.create(menuRequest)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("메뉴를 만들 수 있다")
	@Test
	void create_menu() {
		// given
		MenuGroup menuGroup = new MenuGroup("메뉴 그룹");
		Product product = new Product("상품", new BigDecimal(6000));
		MenuProduct menuProduct = new MenuProduct(product, 2L);
		Menu menuRequest = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));

		when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
		when(productRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(product));
		when(productRepository.findById(any())).thenReturn(Optional.of(product));
		when(purgomalumClient.containsProfanity(anyString())).thenReturn(false);

		Menu menu = new Menu("메뉴", new BigDecimal(10000), menuGroup, true, Collections.singletonList(menuProduct));
		when(menuRepository.save(any())).thenReturn(menu);
		// when
		Menu result = menuService.create(menuRequest);

		// then
		assertThat(result.getName()).isEqualTo("메뉴");
		assertThat(result.getPrice()).isEqualTo(new BigDecimal(10000));
		assertThat(result.getMenuProducts().size()).isSameAs(1);
	}
}
