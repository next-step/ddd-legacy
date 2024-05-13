package utils

import kitchenpos.domain.Menu
import kitchenpos.domain.MenuRepository
import java.util.*

object FakeMenuRepository : MenuRepository {
    private val menuMap = mutableMapOf<UUID, Menu>()

    override fun save(menu: Menu): Menu {
        menuMap[menu.id] = menu

        return menu
    }

    override fun findById(id: UUID): Optional<Menu> {
        return Optional.ofNullable(menuMap[id])
    }

    override fun findAll(): List<Menu> {
        return menuMap.values.toList()
    }

    override fun findAllByIdIn(ids: List<UUID>): List<Menu> {
        return ids.asSequence()
            .mapNotNull { menuMap[it] }
            .toList()
    }

    override fun findAllByProductId(productId: UUID): List<Menu> {
        return menuMap.filterValues { menu ->
            menu.menuProducts.any { it.productId == productId }
        }.values.toList()
    }
}
