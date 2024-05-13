package kitchenpos.domain

import java.util.*

interface MenuRepository {
    fun save(menu: Menu): Menu

    fun findById(id: UUID): Optional<Menu>

    fun findAll(): List<Menu>

    fun findAllByIdIn(ids: List<UUID>): List<Menu>

    fun findAllByProductId(productId: UUID): List<Menu>
}
