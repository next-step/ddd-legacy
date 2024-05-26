package kitchenpos.repository;

import kitchenpos.domain.*;

import java.util.*;

public class InMemoryMenuRepository implements MenuRepository {
    private final BaseInMemoryDao<Menu> dao = new BaseInMemoryDao<>();

    @Override
    public Menu save(Menu menu) {
        return dao.save(menu);
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return dao.findById(id);
    }

    @Override
    public List<Menu> findAll() {
        return dao.findAll();
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return dao.findAllByIdIn(ids);
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        Collection<Menu> menus = dao.getAll();
        List<Menu> result = new ArrayList<>();
        for (Menu menu : menus) {
            for (MenuProduct menuProduct : menu.getMenuProducts()) {
                if (menuProduct.getProductId().equals(productId)) {
                    result.add(menu);
                    break;
                }
            }
        }
        return result;
    }
}
