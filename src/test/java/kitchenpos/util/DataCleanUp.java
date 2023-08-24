package kitchenpos.util;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DataCleanUp implements InitializingBean {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        tableNames = entityManager.getMetamodel().getEntities().stream()
            .filter(entity -> entity.getJavaType().getAnnotation(Entity.class) != null)
            .map(entity -> {
                Table tableAnnotation = entity.getJavaType().getAnnotation(Table.class);
                if (tableAnnotation == null) {
                    return entity.getName();
                }
                if (tableAnnotation.name() == null) {
                    return entity.getName();
                }
                if (tableAnnotation.name().isEmpty()) {
                    return entity.getName();
                }
                return tableAnnotation.name();
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

}
