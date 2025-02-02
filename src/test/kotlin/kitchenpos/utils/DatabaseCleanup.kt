package kitchenpos.utils

import jakarta.persistence.Entity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.Table
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Profile("test")
@Service
class DatabaseCleanup : InitializingBean {

    @PersistenceContext
    private lateinit var em: EntityManager

    private var tableNames: List<String> = ArrayList()

    override fun afterPropertiesSet() {
        tableNames = em.metamodel.entities.stream()
            .filter { entity -> entity.javaType.isAnnotationPresent(Entity::class.java) }
            .map { entity ->
                entity.javaType.getAnnotation(Table::class.java).name
                    .ifBlank {
                        entity.javaType.simpleName
                    }
            }
            .toList()
        println(tableNames)
    }

    @Transactional
    fun execute() {
        em.flush()
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate()
        tableNames.forEach { tableName ->
            em.createNativeQuery("TRUNCATE TABLE ${tableName}").executeUpdate()
        }
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate()
    }
}
