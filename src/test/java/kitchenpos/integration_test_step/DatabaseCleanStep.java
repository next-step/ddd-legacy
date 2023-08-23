package kitchenpos.integration_test_step;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DatabaseCleanStep {
    @PersistenceContext
    private final EntityManager entityManager;
    private final List<String> tableNames;

    public DatabaseCleanStep(
            final EntityManager entityManager
    ) {
        this.tableNames = entityManager.getMetamodel().getEntities().stream()
                .filter(entity -> entity.getJavaType().isAnnotationPresent(Entity.class))
                .map(this::extractTableName)
                .collect(Collectors.toList());
        this.entityManager = entityManager;
    }

    private String extractTableName(EntityType<?> entity) {
        if (entity.getJavaType().isAnnotationPresent(Table.class)) {
            return entity.getJavaType().getAnnotation(Table.class).name();
        }
        return entity.getName();
    }

    @Transactional
    public void clean() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        tableNames.forEach(tableName -> entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate());
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
