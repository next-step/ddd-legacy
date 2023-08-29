package kitchenpos.ui.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DatabaseCleanup implements InitializingBean {

    private static final String ALTER_SEQUENCE_FORMAT = "ALTER TABLE %s ALTER COLUMN %S RESTART WITH 1";

    @PersistenceContext
    private EntityManager entityManager;

    private Set<EntityType<?>> entityTypes;

    @Override
    public void afterPropertiesSet() {
        entityTypes = entityManager.getMetamodel().getEntities().stream()
                .filter(entity -> entity.getJavaType().getAnnotation(Entity.class) != null)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        for (EntityType<?> entityType : entityTypes) {
            Table table = entityType.getJavaType().getAnnotation(Table.class);
            entityManager.createNativeQuery("TRUNCATE TABLE " + table.name()).executeUpdate();
            if (entityType.getIdType().getJavaType().isAssignableFrom(Long.class)) {
                entityManager.createNativeQuery(String.format(
                        ALTER_SEQUENCE_FORMAT,
                        table.name(),
                        entityType.getId(Long.class).getName())
                ).executeUpdate();
            }
        }
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}