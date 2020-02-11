package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

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
        // { id: 1, name: testMenu, price: 16000, groupId: 2, menuProducts: mockMenuProducts }
        createMockMenu();

        // { seq: 1, menuId: 1, productId: 1, quantity: 3 }
        createMockMenuProducts();

        // { seq: 1, menuId: 1, orderId: 1, quantity: 2 }
        createMockOrderLineItems();

        // { tableId: 3, orderLineItems: mockOrderLineItems }
        createMockOrder();

        // { id: 3, empty: false, numberOfGuests: 4, tableGroupId: 33 }
        createOrderTable();

        mockOrderLineItems.add(mockOrderLineItem);
        mockMenuProducts.add(mockMenuProduct);
    }

    @DisplayName("정상적인 order 생성 시도 성공")
    @Test
    public void createOrderSuccess() {
        given(menuDao.countByIdIn(Collections.singletonList(1L))).willReturn(1L);
        given(orderTableDao.findById(3L)).willReturn(Optional.ofNullable(mockOrderTable));
        given(orderDao.save(mockOrder)).willReturn(mockOrder);
        given(orderLineItemDao.save(mockOrderLineItem)).willReturn(mockOrderLineItem);

        Order saved = orderBo.create(mockOrder);

        assertThat(saved.getOrderStatus()).isEqualTo("COOKING");
        assertThat(saved.getOrderedTime().getDayOfMonth()).isEqualTo(LocalDateTime.now().getDayOfMonth());
        assertThat(saved.getOrderLineItems().get(0).getSeq()).isEqualTo(1L);
    }

    @DisplayName("주문 상품 없이 order 생성 시도 시 실패")
    @Test
    public void createOrderFailWithoutOrderLineItems() {
        mockOrder.setOrderLineItems(null);

        assertThatThrownBy(() -> orderBo.create(mockOrder)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴판에 없는 메뉴로 주문 생성 시도 시 실패")
    @Test
    public void createOrderFailWhenNotExistMenu() {
        given(menuDao.countByIdIn(Collections.singletonList(1L))).willReturn(0L);

        assertThatThrownBy(() -> orderBo.create(mockOrder)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않는 주문 테이블에서 주문 생성 시도 시 실패")
    @Test
    public void createOrderFailWithoutOrderTable() {
        given(menuDao.countByIdIn(Collections.singletonList(1L))).willReturn(1L);

        assertThatThrownBy(() -> orderBo.create(mockOrder)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 테이블이 빈 상태에서 주문 생성 시도 시 실패")
    @Test
    public void createOrderFailWithEmptyOrderTable() {
        mockOrderTable.setEmpty(true);
        given(menuDao.countByIdIn(Collections.singletonList(1L))).willReturn(1L);
        given(orderTableDao.findById(3L)).willReturn(Optional.ofNullable(mockOrderTable));

        assertThatThrownBy(() -> orderBo.create(mockOrder)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 전체 조회 시 주문과 관련된 주문 메뉴가 잘 나오는지 확인")
    @Test
    public void getOrderListWithCorrectOrderLineItem() {
        given(orderDao.findAll()).willReturn(Collections.singletonList(mockOrder));
        given(orderLineItemDao.findAllByOrderId(1L)).willReturn(mockOrderLineItems);

        List<Order> orders = orderBo.list();

        assertThat(orders.get(0).getId()).isNotNull();
        assertThat(orders.get(0).getOrderLineItems().get(0).getOrderId()).isEqualTo(1L);
    }

    private void createMockMenu() {
        mockMenu.setId(1L);
        mockMenu.setName("testMenu");
        mockMenu.setPrice(BigDecimal.valueOf(16000));
        mockMenu.setMenuGroupId(2L);
        mockMenu.setMenuProducts(mockMenuProducts);
    }

    private void createMockMenuProducts() {
        mockMenuProduct.setSeq(1L);
        mockMenuProduct.setMenuId(1L);
        mockMenuProduct.setProductId(1L);
        mockMenuProduct.setQuantity(3);
    }

    private void createMockOrder() {
        mockOrder.setId(1L);
        mockOrder.setOrderTableId(3L);
        mockOrder.setOrderLineItems(mockOrderLineItems);
    }

    private void createMockOrderLineItems() {
        mockOrderLineItem.setSeq(1L);
        mockOrderLineItem.setMenuId(1L);
        mockOrderLineItem.setOrderId(1L);
        mockOrderLineItem.setQuantity(2);
    }

    private void createOrderTable() {
        mockOrderTable.setId(3L);
        mockOrderTable.setEmpty(false);
        mockOrderTable.setNumberOfGuests(4);
        mockOrderTable.setTableGroupId(33L);
    }
}
