package kitchenpos.testsupport

import java.util.Optional
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuGroupRepository

class FakeMenuGroupRepository : MenuGroupRepository {
    private val storage: MutableMap<UUID, MenuGroup> = ConcurrentHashMap()
    override fun save(menuGroup: MenuGroup): MenuGroup {
        storage[menuGroup.id] = menuGroup
        return menuGroup
    }

    override fun findAll(): MutableList<MenuGroup> {
        return storage.values.toMutableList()
    }

    override fun findById(menuGroupId: UUID): Optional<MenuGroup> {
        return Optional.ofNullable(storage[menuGroupId])
    }
}