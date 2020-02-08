package kitchenpos.controller;

import kitchenpos.bo.TableGroupBo;
import kitchenpos.model.TableGroup;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class TableGroupRestController {
    private final TableGroupBo tableGroupBo;

    public TableGroupRestController(final TableGroupBo tableGroupBo) {
        this.tableGroupBo = tableGroupBo;
    }

    /**
     * 테이블그룹 생성
     *
     * @param tableGroup
     * @return
     */
    @PostMapping("/api/table-groups")
    public ResponseEntity<TableGroup> create(@RequestBody final TableGroup tableGroup) {
        final TableGroup created = tableGroupBo.create(tableGroup);
        final URI uri = URI.create("/api/table-groups/" + created.getId());
        return ResponseEntity.created(uri)
                .body(created)
                ;
    }

    /**
     * 테이블그룹 삭제
     *
     * @param tableGroupId
     * @return
     */
    @DeleteMapping("/api/table-groups/{tableGroupId}")
    public ResponseEntity<Void> delete(@PathVariable final Long tableGroupId) {
        tableGroupBo.delete(tableGroupId);
        return ResponseEntity.noContent()
                .build()
                ;
    }
}
