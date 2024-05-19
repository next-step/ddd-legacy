package fixtures;

import kitchenpos.domain.FakeMenuGroupRepository;
import kitchenpos.domain.FakeMenuRepository;
import kitchenpos.domain.FakeOrderRepository;
import kitchenpos.domain.FakeOrderTableRepository;
import kitchenpos.domain.FakeProductRepository;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.ProductRepository;

public class TestContainers {

    public MenuGroupRepository menuGroupRepository = new FakeMenuGroupRepository();
    public ProductRepository productRepository = new FakeProductRepository();
    public MenuRepository menuRepository = new FakeMenuRepository();
    public OrderRepository orderRepository = new FakeOrderRepository();
    public OrderTableRepository orderTableRepository = new FakeOrderTableRepository();
}
