package kitchenpos.controller;

import kitchenpos.bo.MenuBo;
import kitchenpos.model.Menu;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
public class MenuRestController {
    private final MenuBo menuBo;

    public MenuRestController(final MenuBo menuBo) {
        this.menuBo = menuBo;
    }

    /**
     * 메뉴 생성
     *
     * @param menu
     * @return
     */
    @PostMapping("/api/menus")
    public ResponseEntity<Menu> create(@RequestBody final Menu menu) {
        final Menu created = menuBo.create(menu);
        final URI uri = URI.create("/api/menus/" + created.getId());
        return ResponseEntity.created(uri)
                .body(created)
                ;
    }

    /**
     * 전체 메뉴 리스트 조회
     *
     * @return
     */
    @GetMapping("/api/menus")
    public ResponseEntity<List<Menu>> list() {
        return ResponseEntity.ok()
                .body(menuBo.list())
                ;
    }
}
