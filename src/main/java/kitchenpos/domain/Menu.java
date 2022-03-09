package kitchenpos.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Table(name = "menu")
@Entity
public class Menu {
	@Column(name = "id", columnDefinition = "varbinary(16)")
	@Id
	private UUID id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "price", nullable = false)
	private BigDecimal price;

	@ManyToOne(optional = false)
	@JoinColumn(
		name = "menu_group_id",
		columnDefinition = "varbinary(16)",
		foreignKey = @ForeignKey(name = "fk_menu_to_menu_group")
	)
	private MenuGroup menuGroup;

	@Column(name = "displayed", nullable = false)
	private boolean displayed;

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(
		name = "menu_id",
		nullable = false,
		columnDefinition = "varbinary(16)",
		foreignKey = @ForeignKey(name = "fk_menu_product_to_menu")
	)
	private List<MenuProduct> menuProducts;

	@Transient
	private UUID menuGroupId;

	public Menu() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(final UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(final BigDecimal price) {
		this.price = price;
	}

	public MenuGroup getMenuGroup() {
		return menuGroup;
	}

	public void setMenuGroup(final MenuGroup menuGroup) {
		this.menuGroup = menuGroup;
	}

	public boolean isDisplayed() {
		return displayed;
	}

	public void setDisplayed(final boolean displayed) {
		this.displayed = displayed;
	}

	public List<MenuProduct> getMenuProducts() {
		return menuProducts;
	}

	public void setMenuProducts(final List<MenuProduct> menuProducts) {
		this.menuProducts = menuProducts;
	}

	public UUID getMenuGroupId() {
		return menuGroupId;
	}

	public void setMenuGroupId(final UUID menuGroupId) {
		this.menuGroupId = menuGroupId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Menu menu = (Menu) o;
		return isDisplayed() == menu.isDisplayed() && Objects.equals(getId(), menu.getId()) && Objects.equals(getName(), menu.getName()) && Objects.equals(getPrice(), menu.getPrice()) && Objects.equals(getMenuGroup(), menu.getMenuGroup()) && Objects.equals(getMenuProducts(), menu.getMenuProducts()) && Objects.equals(getMenuGroupId(), menu.getMenuGroupId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getName(), getPrice(), getMenuGroup(), isDisplayed(), getMenuProducts(), getMenuGroupId());
	}
}
