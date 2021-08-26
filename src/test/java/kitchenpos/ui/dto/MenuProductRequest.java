package kitchenpos.ui.dto;

import java.util.UUID;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductRequest {

    private Long seq;
    private ProductRequest product;
    private long quantity;

    private UUID productId;

    public MenuProductRequest(final MenuProduct menuProduct) {
        this(menuProduct.getQuantity(), menuProduct.getProduct());
    }

    public MenuProductRequest(final long quantity, final Product product) {
        this.quantity = quantity;
        this.product = new ProductRequest(product);

        this.productId = product.getId();
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(final Long seq) {
        this.seq = seq;
    }

    public ProductRequest getProduct() {
        return product;
    }

    public void setProduct(final ProductRequest product) {
        this.product = product;
    }

    public void setProduct(final Product product) {
        this.product = new ProductRequest(product);
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(final long quantity) {
        this.quantity = quantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(final UUID productId) {
        this.productId = productId;
    }
}
