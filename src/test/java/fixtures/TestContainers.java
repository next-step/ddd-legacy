package fixtures;

import kitchenpos.domain.FakeMenuGroupRepository;
import kitchenpos.domain.FakeMenuRepository;
import kitchenpos.domain.FakeProductRepository;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.ProductRepository;

public class TestContainers {

    public MenuGroupRepository menuGroupRepository = new FakeMenuGroupRepository();
    public ProductRepository productRepository = new FakeProductRepository();
    public FakeMenuRepository menuRepository = new FakeMenuRepository();
}
