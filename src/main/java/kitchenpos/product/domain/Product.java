package kitchenpos.product.domain;

import kitchenpos.domain.Name;
import kitchenpos.menu.menu.domain.Price;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "product")
@Entity
public class Product {
    @Column(name = "id", columnDefinition = "binary(16)")
    @Id
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    private Price price;

    public Product(Name name, Price price) {
        validatePrice(price);
        validateName(name);
        this.price = price;
    }

    private void validatePrice(Price price) {
        if (price == null) {
            throw new IllegalArgumentException("상품 가격을 입력해주세요.");
        }
    }

    private void validateName(Name name) {
        if (name == null) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
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
        return this.price.getPrice();
    }

    public void changePrice(BigDecimal price) {
        this.price = new Price(price);
    }
}
