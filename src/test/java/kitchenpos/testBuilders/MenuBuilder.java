package kitchenpos.testBuilders;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static kitchenpos.testBuilders.MenuGroupBuilder.aDefaultMenuGroup;
import static kitchenpos.testBuilders.MenuProductBuilder.aDefaultMenuProduct;

public class MenuBuilder {
	public static final String DEFAULT_MENU_NAME = "짬뽕";
	public static final BigDecimal DEFAULT_MENU_PRICE = BigDecimal.valueOf(1000);
	public static final boolean DEFAULT_MENU_DISPLAYED = true;
	public static final List<MenuProduct> DEFAULT_MENU_PRODUCTS = Collections.singletonList(aDefaultMenuProduct().build());
	private UUID id;
	private String name;
	private BigDecimal price;
	private MenuGroup menuGroup;
	private boolean displayed;
	private List<MenuProduct> menuProducts;
	private MenuBuilder() {
	}

	public static MenuBuilder aMenu() {
		return new MenuBuilder();
	}

	public static MenuBuilder aDefaultMenu() {
		return aMenu()
				.withId(UUID.randomUUID())
				.withName(DEFAULT_MENU_NAME)
				.withPrice(DEFAULT_MENU_PRICE)
				.withMenuGroup(aDefaultMenuGroup().build())
				.withDisplayed(DEFAULT_MENU_DISPLAYED)
				.withMenuProducts(DEFAULT_MENU_PRODUCTS);
	}

	public static MenuBuilder aMenuThatSamePriceWithMenuProductsTotalPrice() {
		BigDecimal defaultMenuProductsTotalPrice = calculateDefaultMenuProductsTotalPrice();

		return aDefaultMenu()
				.withMenuProducts(DEFAULT_MENU_PRODUCTS)
				.withPrice(defaultMenuProductsTotalPrice);
	}

	public static MenuBuilder aMenuThatMoreExpensivePriceThanMenuProductsTotalPrice() {
		BigDecimal samePriceWithMenuProductsTotalPrice = aMenuThatSamePriceWithMenuProductsTotalPrice().price;

		return aMenuThatSamePriceWithMenuProductsTotalPrice()
				.withPrice(samePriceWithMenuProductsTotalPrice.add(BigDecimal.ONE));
	}

	private static BigDecimal calculateDefaultMenuProductsTotalPrice() {
		BigDecimal price = BigDecimal.ZERO;
		for (MenuProduct menuProduct : MenuBuilder.DEFAULT_MENU_PRODUCTS) {
			BigDecimal productPrice = menuProduct.getProduct().getPrice();
			long menuProductQuantity = menuProduct.getQuantity();
			BigDecimal totalPriceOfCurrentProduct = productPrice.multiply(BigDecimal.valueOf(menuProductQuantity));
			price = price.add(totalPriceOfCurrentProduct);
		}
		return price;
	}

	public MenuBuilder withId(UUID id) {
		this.id = id;
		return this;
	}

	public MenuBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public MenuBuilder withPrice(BigDecimal price) {
		this.price = price;
		return this;
	}

	public MenuBuilder withMenuGroup(MenuGroup menuGroup) {
		this.menuGroup = menuGroup;
		return this;
	}

	public MenuBuilder withDisplayed(boolean displayed) {
		this.displayed = displayed;
		return this;
	}

	public MenuBuilder withMenuProducts(List<MenuProduct> menuProducts) {
		this.menuProducts = menuProducts;
		return this;
	}

	public Menu build() {
		Menu menu = new Menu();
		menu.setId(id);
		menu.setName(name);
		menu.setPrice(price);
		menu.setMenuGroup(menuGroup);
		menu.setDisplayed(displayed);
		menu.setMenuProducts(menuProducts);
		return menu;
	}
}
