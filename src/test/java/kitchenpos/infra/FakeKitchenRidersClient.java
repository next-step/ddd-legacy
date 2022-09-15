package kitchenpos.infra;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakeKitchenRidersClient implements KitchenridersClient {

    private final List<Kitchenrider> kitchenRiders = new ArrayList<>();

    @Override
    public void requestDelivery(final UUID orderId, final BigDecimal amount, final String deliveryAddress) {
        final Kitchenrider kitchenrider = new Kitchenrider(orderId, amount, deliveryAddress);
        kitchenRiders.add(kitchenrider);
    }

    public Kitchenrider findKitchenRider(UUID orderId) {
        return kitchenRiders.stream().filter(kitchenrider -> kitchenrider.getOrderId() == orderId).findFirst().get();
    }
}
