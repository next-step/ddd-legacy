package kitchenpos.testsupport

import java.util.Optional
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kitchenpos.domain.Menu
import kitchenpos.domain.MenuRepository

class FakeMenuRepository : MenuRepository {
    private val storage: MutableMap<UUID, Menu> = ConcurrentHashMap()

    override fun save(menu: Menu): Menu {
        storage[menu.id] = menu
        return menu
    }

    override fun findAll(): MutableList<Menu> {
        return storage.values.toMutableList()
    }

    override fun findAllByIdIn(list: MutableList<UUID>): MutableList<Menu> {
        return storage.values
            .filter { it.id in list }
            .toMutableList()
    }

    override fun findAllByProductId(productId: UUID): MutableList<Menu> {
        return storage.values
            .filter {  menu ->
                menu.menuProducts.any { menuProduct ->
                    menuProduct.product.id == productId
                }
            }
            .toMutableList()
    }

    override fun findById(menuId: UUID): Optional<Menu> {
        return Optional.ofNullable(storage[menuId])
    }
}