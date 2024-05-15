package kitchenpos.application.fake

import kitchenpos.domain.Menu
import kitchenpos.domain.MenuRepository
import java.util.*

class FakeMenuRepository : MenuRepository {
    private val menus = mutableMapOf<UUID, Menu>()

    override fun save(entity: Menu): Menu {
        menus[entity.id] = entity
        return entity
    }

    override fun findById(id: UUID): Optional<Menu> {
        return Optional.ofNullable(menus[id])
    }

    override fun findAllByIdIn(ids: MutableList<UUID>): MutableList<Menu> {
        return menus.filter {
            it.key in ids
        }.map {
            it.value
        }.toMutableList()
    }

    override fun findAllByProductId(productId: UUID?): MutableList<Menu> {
        return menus.filter { menu ->
            menu.value.menuProducts.map {
                    menuProduct ->
                menuProduct.productId
            }.contains(productId)
        }.map {
            it.value
        }.toMutableList()
    }

    override fun findAll(): MutableList<Menu> {
        return menus.values.toMutableList()
    }
}
