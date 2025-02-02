package kitchenpos.application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.transaction.Transactional;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@SpringBootTest
@Transactional
class OrderTableServiceTest {
    @Autowired
    private OrderTableRepository orderTableRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderTableService orderTableService;

    @DisplayName("주문 테이블을 생성 할 수 있다.")
    @Test
    void create() {
        //given
        OrderTable orderTable = new OrderTable();
        orderTable.setName("1번");

        //when
        OrderTable result = orderTableService.create(orderTable);

        //then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo("1번");
        Assertions.assertThat(result.isOccupied()).isFalse();
        Assertions.assertThat(result.getNumberOfGuests()).isZero();
    }

    @DisplayName("주문 테이블 생성시, 테이블 명이 null 이면 IllegalArgumentException 예외를 발생한다.")
    @Test
    void canNotCreateOrderTableWithNullName() {
        //given
        OrderTable orderTable = new OrderTable();
        orderTable.setName(null);

        //when

        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()->orderTableService.create(orderTable));
    }

    @DisplayName("주문 테이블 생성시, 테이블 명이 비어있으면 IllegalArgumentException 예외를 발생한다.")
    @Test
    void canNotCreateOrderTableWithEmptyName() {
        //given
        OrderTable orderTable = new OrderTable();
        orderTable.setName("");

        //when

        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()->orderTableService.create(orderTable));
    }

    @DisplayName("주문 테이블에 앉을 수 있다.")
    @Test
    void sit() {
        //given
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTableRepository.save(orderTable);

        //when
        OrderTable result = orderTableService.sit(orderTable.getId());

        //then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.isOccupied()).isTrue();

    }

    @DisplayName("주문 테이블에 앉을 때, 테이블 아이디가 존재 하지 앟는다면 NoSuchElementException 예외를 발생한다.")
    @Test
    void canNotSitIfOrderTableIdIsNotExist() {
        //given
        UUID nonExistId = UUID.randomUUID();

        //when

        //then
        Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(()->orderTableService.sit(nonExistId));
    }

    @DisplayName("주문 테이블을 정리할 수 있다.")
    @Test
    void clear() {
    }

    @DisplayName("주문 테이블을 정리할 때, 테이블 아이디가 존재 하지 앟는다면 NoSuchElementException 예외를 발생한다")
    @Test
    void canNotClearIfOrderTableIdIsNotExist() {
        //given
        UUID nonExistId = UUID.randomUUID();

        //when

        //then
        Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(()->orderTableService.clear(nonExistId));
    }

    @DisplayName("주문 테이블을 정리할 때, 완료되지 않은 주문이 존재하면 테이블을 정리할 수 없다.")
    @Test
    void cannotClearTableIfOrderIsNotCompleted() {
        // given
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번");
        orderTable.setOccupied(true);
        orderTableRepository.save(orderTable);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderTable(orderTable);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setType(OrderType.EAT_IN);
        orderRepository.save(order);

        // when

        // then
        Assertions.assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderTableService.clear(orderTable.getId()));
    }


    @DisplayName("테이블 인원 수를 변경 할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        // given
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setOccupied(true);
        orderTable.setNumberOfGuests(3);
        orderTableRepository.save(orderTable);

        OrderTable changedTable = new OrderTable();
        changedTable.setNumberOfGuests(4);

        // when
        OrderTable result = orderTableService.changeNumberOfGuests(orderTable.getId(), changedTable);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getNumberOfGuests()).isEqualTo(4);
    }


    @DisplayName("테이블 인원 수 변경 시, 인원 수가 0 미만이면 IllegalArgumentException 예외가 발생한다.")
    @Test
    void canNotChangeNumberOfGuestsToNegative() {
        // given
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setNumberOfGuests(-1);

        // when

        // then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable)); // ✅ 예외 발생 검증
    }

    @DisplayName("테이블 인원 수 변경 시, 존재하지 않는 테이블의 인원 수를 변경하면 NoSuchElementException 예외가 발생한다.")
    @Test
    void canNotChangeGuestsIfNonExistOrderTable() {
        // given
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setNumberOfGuests(3);

        // when

        // then
        Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), orderTable));
    }

    @DisplayName("테이블 인원 수 변경 시, 사용 중이지 않은 테이블의 인원 수를 변경하면 NoSuchElementException 예외가 발생한다.")
    @Test
    void canNotChangeGuestsIfTableIsNotOccupied() {
        // given
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setOccupied(false);
        orderTable.setNumberOfGuests(3);
        orderTableRepository.save(orderTable);

        OrderTable changedTable = new OrderTable();
        changedTable.setNumberOfGuests(2);

        // when

        // then
        Assertions.assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), changedTable));
    }



    @DisplayName("모든 주문 테이블을 조회 할 수 있다.")
    @Test
    void findAll() {
        // given
        OrderTable orderTable1 = new OrderTable();
        orderTable1.setId(UUID.randomUUID());
        orderTable1.setName("1번");
        orderTable1.setOccupied(true);
        orderTableRepository.save(orderTable1);

        OrderTable orderTable2 = new OrderTable();
        orderTable2.setId(UUID.randomUUID());
        orderTable2.setName("2번");
        orderTable2.setOccupied(true);
        orderTableRepository.save(orderTable2);

        //when
        List<OrderTable> result = orderTableService.findAll();

        //then
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result)
                .extracting(OrderTable::getName)
                .containsExactlyInAnyOrder("1번", "2번");
    }
}