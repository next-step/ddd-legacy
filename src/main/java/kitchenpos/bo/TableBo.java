package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class TableBo {
    private final OrderDao orderDao;
    private final OrderTableDao orderTableDao;

    public TableBo(final OrderDao orderDao, final OrderTableDao orderTableDao) {
        this.orderDao = orderDao;
        this.orderTableDao = orderTableDao;
    }

    /**
     * 테이블 생성 및 수정(테이블그룹, 게스트 수, 공석여부)
     *
     * @param orderTable
     * @return
     */
    @Transactional
    public OrderTable create(final OrderTable orderTable) {
        return orderTableDao.save(orderTable);
    }

    /**
     * 전체 테이블 리스트 조회
     *
     * @return
     */
    public List<OrderTable> list() {
        return orderTableDao.findAll();
    }

    /**
     * 테이블 공석여부 변경
     *
     * @param orderTableId
     * @param orderTable
     * @return
     */
    @Transactional
    public OrderTable changeEmpty(final Long orderTableId, final OrderTable orderTable) {
        final OrderTable savedOrderTable = orderTableDao.findById(orderTableId) // 테이블 조회
                .orElseThrow(IllegalArgumentException::new);

        if (Objects.nonNull(savedOrderTable.getTableGroupId())) { // 해당 테이블이 테이블그룹에 포함된 경우 변경할 수 없다.
            throw new IllegalArgumentException();
        }

        if (orderDao.existsByOrderTableIdAndOrderStatusIn( // 테이블에서 발생한 주문들의 주문상태가 완료인 경우에만 변경할 수 있다.
                orderTableId, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))) {
            throw new IllegalArgumentException();
        }

        savedOrderTable.setEmpty(orderTable.isEmpty()); // 주문상태 세팅

        return orderTableDao.save(savedOrderTable); // 테이블 수정
    }

    /**
     * 테이블 게스트 수 수정
     *
     * @param orderTableId
     * @param orderTable
     * @return
     */
    @Transactional
    public OrderTable changeNumberOfGuests(final Long orderTableId, final OrderTable orderTable) {
        final int numberOfGuests = orderTable.getNumberOfGuests();

        if (numberOfGuests < 0) { // 테이블의 게스"트 수는 0명 이상이다.
            throw new IllegalArgumentException();
        }

        final OrderTable savedOrderTable = orderTableDao.findById(orderTableId) // 테이블 조회
                .orElseThrow(IllegalArgumentException::new);

        if (savedOrderTable.isEmpty()) { // 테이블이 공석 상태라면 에러.
            throw new IllegalArgumentException();
        }

        savedOrderTable.setNumberOfGuests(numberOfGuests);

        return orderTableDao.save(savedOrderTable); // 테이블 수정
    }
}
