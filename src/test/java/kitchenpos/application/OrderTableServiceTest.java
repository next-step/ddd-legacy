package kitchenpos.application;

import kitchenpos.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderTableServiceTest {

    @Autowired
    private OrderTableService orderTableService;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderRepository orderRepository;

    private OrderTable dummyOrderTable;

    private Product dummyproduct;

    private Menu dummymenu;

    private MenuProduct dummyMenuProduct;

    private MenuGroup dummyMenuGroup;

    private Order dummyOrder;

    @BeforeEach
    void setUp() {
        dummyOrderTable = new OrderTable();
        dummyOrderTable.setId(UUID.randomUUID());
        dummyOrderTable.setName("100번");
        dummyOrderTable.setNumberOfGuests(0);

        dummyproduct = productRepository.findById(UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10"))
                .orElseThrow(NoSuchElementException::new);

        dummyMenuProduct = new MenuProduct();
        dummyMenuProduct.setProduct(dummyproduct);

        dummyMenuGroup = new MenuGroup();
        dummyMenuGroup.setId(UUID.randomUUID());
        dummyMenuGroup.setName("추천 메뉴");

        dummymenu = new Menu();
        dummymenu.setId(UUID.randomUUID());
        dummymenu.setName("치킨");
        dummymenu.setPrice(BigDecimal.valueOf(15000));
        dummymenu.setDisplayed(false);
        dummymenu.setMenuGroup(dummyMenuGroup);
        dummymenu.setMenuProducts(Arrays.asList(dummyMenuProduct));

        dummyOrder = new Order();
        dummyOrder.setId(UUID.randomUUID());
        dummyOrder.setType(OrderType.EAT_IN);
        dummyOrder.setOrderDateTime(LocalDateTime.now());

    }

    @DisplayName("주문 테이블 등록 - 성공")
    @Test
    void createOrderTableSuccess() {
        // Given
        final OrderTable request = new OrderTable();
        request.setName("최상의 테이블");

        // When
        final OrderTable data = orderTableService.create(request);

        // Then
        final OrderTable orderTable = orderTableRepository.findById(data.getId())
                .orElseThrow(NoSuchElementException::new);

        assertThat(orderTable.getId()).isEqualTo(data.getId());
        assertThat(orderTable.getName()).isEqualTo(data.getName());
    }

    @DisplayName("주문 테이블 등록 - 등록 실패")
    @ParameterizedTest
    @NullSource
    @EmptySource
    void createMenuGroupFail(final String name) {
        // Give
        final OrderTable request = new OrderTable();
        request.setName(name);

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.create(request));
    }

    @DisplayName("주문 테이블 - 착석 처리")
    @Test
    void orderTableSit() {
        // Given
        dummyOrderTable.setEmpty(true);
        final OrderTable data = orderTableRepository.save(dummyOrderTable);

        // When
        final OrderTable result = orderTableService.sit(data.getId());

        // Then
        assertThat(result.getId()).isEqualTo(data.getId());
        assertThat(result.isEmpty()).isFalse();
    }

    @DisplayName("주문 테이블 - 테이블 정리 성공")
    @Test
    void orderTableClearSuccess() {
        // Given
        final MenuGroup menuGroup = menuGroupRepository.save(dummyMenuGroup);
        dummymenu.setMenuGroup(menuGroup);

        final Menu menu = menuRepository.save(dummymenu);

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(1);

        dummyOrderTable.setEmpty(false);
        dummyOrderTable.setNumberOfGuests(10);
        final OrderTable data = orderTableRepository.save(dummyOrderTable);

        dummyOrder.setStatus(OrderStatus.COMPLETED);
        dummyOrder.setOrderLineItems(Arrays.asList(orderLineItem));
        dummyOrder.setOrderTable(data);

        final Order order = orderRepository.save(dummyOrder);


        // When
        final OrderTable actual = orderTableService.clear(data.getId());

        // Then
        assertThat(actual.getId()).isEqualTo(data.getId());
        assertThat(actual.isEmpty()).isTrue();
        assertThat(actual.getNumberOfGuests()).isZero();
    }

    @DisplayName("주문 테이블 - 테이블 정리 실패, 주문 상태가 주문 완료가 아님")
    @Test
    void orderTableClearFail() {
        // Given
        final MenuGroup menuGroup = menuGroupRepository.save(dummyMenuGroup);
        dummymenu.setMenuGroup(menuGroup);

        final Menu menu = menuRepository.save(dummymenu);

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(1);

        dummyOrderTable.setEmpty(false);
        dummyOrderTable.setNumberOfGuests(10);
        final OrderTable data = orderTableRepository.save(dummyOrderTable);

        dummyOrder.setStatus(OrderStatus.SERVED);
        dummyOrder.setOrderLineItems(Arrays.asList(orderLineItem));
        dummyOrder.setOrderTable(data);

        final Order order = orderRepository.save(dummyOrder);

        // When, Then
        Assertions.assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderTableService.clear(data.getId()));
    }

    @DisplayName("주문 테이블 전체 조회")
    @Test
    void findAll() {
        // Given
        orderTableRepository.save(dummyOrderTable);

        // When
        final List<OrderTable> list = orderTableService.findAll();

        // Then
        assertThat(list).isNotEmpty();
    }
}
