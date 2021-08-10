package kitchenpos.product.step;

public class ProductSaveRequest {

    private String name;
    private int price;

    public ProductSaveRequest(final String name, final int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
