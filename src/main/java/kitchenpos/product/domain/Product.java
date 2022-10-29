package kitchenpos.product.domain;

import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "product")
@Entity
public class Product {
    @Column(name = "id", columnDefinition = "binary(16)")
    @Id
    private UUID id;

    private Name name;

    @Embedded
    private Price price;

    protected Product() {
    }

    public Product(UUID id, Name name, Price price) {
        validatePrice(price);
        this.price = price;
        this.id = id;
        this.name = name;
    }

    private void validatePrice(Price price) {
        if (price == null) {
            throw new IllegalArgumentException("상품 가격을 입력해주세요.");
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return this.price.getPrice();
    }

    public void changePrice(BigDecimal price) {
        this.price = new Price(price);
    }
}
