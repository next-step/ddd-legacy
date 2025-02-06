package kitchenpos.domain

import java.util.*

class InMemoryMenuRepositoryFake : MenuRepository {
    private val menus: MutableMap<UUID, Menu> = mutableMapOf()

    override fun save(menu: Menu): Menu {
        menus[menu.id] = menu
        return menu
    }

    override fun findById(id: UUID?): Optional<Menu> {
        return Optional.ofNullable(menus[id])
    }

    override fun findAll(): MutableList<Menu> {
        return menus.values.toMutableList()
    }

    override fun findAllByIdIn(ids: MutableList<UUID>): MutableList<Menu> {
        return ids.mapNotNull { menus[it] }.toMutableList()
    }

    override fun findAllByProductId(productId: UUID?): List<Menu> {
        return menus.values.filter { menu ->
            menu.menuProducts != null && menu.menuProducts.any { menuProduct ->
                menuProduct.productId == productId
            }
        }
    }
}
