package kitchenpos.domain

import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import java.util.*

@NoRepositoryBean
interface BaseRepository<T, ID> : Repository<T, ID> {
    fun findById(id: ID): Optional<T>?

    fun <S : T?> save(entity: S): S

    fun findAll(): MutableList<T>
}
