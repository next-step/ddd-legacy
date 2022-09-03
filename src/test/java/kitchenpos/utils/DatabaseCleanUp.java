package kitchenpos.utils;

import com.google.common.base.CaseFormat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatabaseCleanUp implements InitializingBean {

    private final EntityManager em;
    private List<String> tableNames;

    public DatabaseCleanUp(EntityManager em) {
        this.em = em;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        tableNames = em.getMetamodel().getEntities().stream()
                .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
                .map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, getTableName(e)))
                .collect(Collectors.toList());
    }

    private <T> String getTableName(EntityType<T> entityType) {
        Table table = entityType.getJavaType().getAnnotation(Table.class);
        if (table != null) {
            return table.name();
        }
        return entityType.getName();
    }

    @Transactional
    public void execute() {
        em.flush();
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        for (String tableName : tableNames) {
            em.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
