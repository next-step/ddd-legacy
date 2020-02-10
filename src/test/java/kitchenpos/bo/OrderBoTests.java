package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderBoTests {

    @Mock
    private MenuDao menuDao;
    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderLineItemDao orderLineItemDao;
    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private OrderBo orderBo;

    private Menu mockMenu = new Menu();
    private Order mockOrder = new Order();
    private OrderTable mockOrderTable = new OrderTable();
    private OrderLineItem mockOrderLineItem = new OrderLineItem();
    private MenuProduct mockMenuProduct = new MenuProduct();
    private List<OrderLineItem> mockOrderLineItems = new ArrayList<>();
    private List<MenuProduct> mockMenuProducts = new ArrayList<>();

    @BeforeEach
    public void setup() {
        createMockMenu();
        createMockMenuProducts();
        createMockOrder();
        createMockOrderLineItems();
        createOrderTable();

        mockOrderLineItems.add(mockOrderLineItem);
        mockMenuProducts.add(mockMenuProduct);
    }

    private void createMockMenu() {
        mockMenu.setId(1L);
        mockMenu.setName("testMenu");
        mockMenu.setPrice(BigDecimal.valueOf(16000));
        mockMenu.setMenuGroupId(2L);
        mockMenu.setMenuProducts(mockMenuProducts);
    }

    private void createMockMenuProducts() {
        mockMenuProduct.setQuantity(3);
        mockMenuProduct.setProductId(1L);
        mockMenuProduct.setSeq(1L);
        mockMenuProduct.setMenuId(1L);
    }

    private void createMockOrder() {
        mockOrder.setId(1L);
        mockOrder.setOrderedTime(LocalDateTime.of(2020, 2, 10, 22, 22));
        mockOrder.setOrderLineItems(mockOrderLineItems);
        mockOrder.setOrderStatus(String.valueOf(OrderStatus.COOKING));
        mockOrder.setOrderTableId(3L);
    }

    private void createMockOrderLineItems() {
        mockOrderLineItem.setMenuId(1L);
        mockOrderLineItem.setOrderId(1L);
        mockOrderLineItem.setQuantity(2);
        mockOrderLineItem.setSeq(1L);
    }

    private void createOrderTable() {
        mockOrderTable.setId(3L);
        mockOrderTable.setEmpty(false);
        mockOrderTable.setNumberOfGuests(4);
        mockOrderTable.setTableGroupId(33L);
    }
}
