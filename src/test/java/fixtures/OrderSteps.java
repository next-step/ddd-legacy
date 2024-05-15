package fixtures;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;

import java.math.BigDecimal;
import java.util.List;


public class OrderSteps {

    private MenuGroupRepository menuGroupRepository;

    private MenuRepository menuRepository;

    private OrderTableRepository orderTableRepository;

    public OrderSteps(MenuGroupRepository menuGroupRepository, MenuRepository menuRepository, OrderTableRepository orderTableRepository) {
        this.menuGroupRepository = menuGroupRepository;
        this.menuRepository = menuRepository;
        this.orderTableRepository = orderTableRepository;
    }

    public MenuGroup 메뉴그룹_생성한다() {
        return menuGroupRepository.save(new MenuGroupBuilder().withName("한 마리 메뉴").build());
    }


    public Menu 메뉴그룹에_소속될_메뉴를_생성한다(MenuGroup menuGroup) {
        return menuRepository.save(new MenuBuilder()
                .withMenuGroup(menuGroup)
                .with("치킨", BigDecimal.valueOf(10_000))
                .build());
    }


    public Order 주문을_생성한다(Menu menu, OrderType orderType) {

        OrderLineItem orderLineItems = new OrderLineItemBuilder()
                .withMenu(menu)
                .withPrice(BigDecimal.valueOf(10_000))
                .build();

        OrderTable orderTable = orderTableRepository.save(new OrderTableBuilder()
                .anOrderTable()
                .build());

        return new OrderBuilder()
                .withOrderType(orderType)
                .withOrderLineItems(List.of(orderLineItems))
                .withOrderTable(orderTable)
                .build();
    }

}

