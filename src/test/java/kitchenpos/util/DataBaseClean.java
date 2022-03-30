package kitchenpos.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
@ActiveProfiles("test")
public class DataBaseClean implements InitializingBean {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames = new ArrayList<>();

    @Override
    public void afterPropertiesSet() {
        List<Object[]> tables = entityManager.createNativeQuery("SHOW TABLES")
                .getResultList();

        for (Object[] table : tables) {
            String tableName = (String) table[0];
            tableNames.add(tableName);
        }
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        for (String tableName : tableNames) {
            truncateTable(tableName);
        }

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();

    }

    private void truncateTable(String tableName) {
        if (tableName.contains("flyway")) {
            return;
        }
        entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
    }
}
