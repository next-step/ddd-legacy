package kitchenpos.controller;

import kitchenpos.bo.TableBo;
import kitchenpos.model.OrderTable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class TableRestController {
    private final TableBo tableBo;

    public TableRestController(final TableBo tableBo) {
        this.tableBo = tableBo;
    }

    /**
     * 테이블 생성 및 수정(테이블그룹, 게스트 수, 공석여부)
     *
     * @param orderTable
     * @return
     */
    @PutMapping("/api/tables")
    public ResponseEntity<OrderTable> create(@RequestBody final OrderTable orderTable) {
        final OrderTable created = tableBo.create(orderTable);
        final URI uri = URI.create("/api/tables/" + created.getId());
        return ResponseEntity.created(uri)
                .body(created)
                ;
    }

    /**
     * 전체 테이블 리스트 조회
     *
     * @return
     */
    @GetMapping("/api/tables")
    public ResponseEntity<List<OrderTable>> list() {
        return ResponseEntity.ok()
                .body(tableBo.list())
                ;
    }

    /**
     * 테이블 공석여부 변경
     *
     * @param orderTableId
     * @param orderTable
     * @return
     */
    @PutMapping("/api/tables/{orderTableId}/empty")
    public ResponseEntity<OrderTable> changeEmpty(
            @PathVariable final Long orderTableId,
            @RequestBody final OrderTable orderTable
    ) {
        return ResponseEntity.ok()
                .body(tableBo.changeEmpty(orderTableId, orderTable))
                ;
    }

    /**
     * 테이블 게스트 수 수정
     *
     * @param orderTableId
     * @param orderTable
     * @return
     */
    @PutMapping("/api/tables/{orderTableId}/number-of-guests")
    public ResponseEntity<OrderTable> changeNumberOfGuests(
            @PathVariable final Long orderTableId,
            @RequestBody final OrderTable orderTable
    ) {
        return ResponseEntity.ok()
                .body(tableBo.changeNumberOfGuests(orderTableId, orderTable))
                ;
    }
}
