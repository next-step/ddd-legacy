package kitchenpos.menu.menu.ui;

import kitchenpos.menu.menu.application.MenuCreateService;
import kitchenpos.menu.menu.application.ChangeMenuPriceService;
import kitchenpos.menu.menu.application.MenuDisplayService;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.dto.request.ChangeMenuPriceRequest;
import kitchenpos.menu.menu.dto.request.MenuRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/menus")
@RestController
public class MenuRestController {
    private final MenuCreateService menuCreateService;
    private final ChangeMenuPriceService changeMenuPriceService;
    private final MenuDisplayService menuDisplayService;

    public MenuRestController(final MenuCreateService menuCreateService, final ChangeMenuPriceService changeMenuPriceService, final MenuDisplayService menuDisplayService) {
        this.menuCreateService = menuCreateService;
        this.changeMenuPriceService = changeMenuPriceService;
        this.menuDisplayService = menuDisplayService;

    }

    @PostMapping
    public ResponseEntity<Menu> create(@RequestBody final MenuRequest request) {
        final Menu response = menuCreateService.create(request);
        return ResponseEntity.created(URI.create("/api/menus/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{menuId}/price")
    public ResponseEntity<Menu> changePrice(@PathVariable final UUID menuId, @RequestBody final ChangeMenuPriceRequest request) {
        return ResponseEntity.ok(changeMenuPriceService.changePrice(menuId, request));
    }

    @PutMapping("/{menuId}/display")
    public ResponseEntity<Menu> display(@PathVariable final UUID menuId) {
        return ResponseEntity.ok(menuDisplayService.display(menuId));
    }

    @PutMapping("/{menuId}/hide")
    public ResponseEntity<Menu> hide(@PathVariable final UUID menuId) {
        return ResponseEntity.ok(menuDisplayService.hide(menuId));
    }

    @GetMapping
    public ResponseEntity<List<Menu>> findAll() {
        return ResponseEntity.ok(menuCreateService.findAll());
    }
}
