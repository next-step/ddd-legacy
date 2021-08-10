package kitchenpos.product.step;

public class ProductChangePriceRequest {

    private int price;

    public ProductChangePriceRequest(final int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}
