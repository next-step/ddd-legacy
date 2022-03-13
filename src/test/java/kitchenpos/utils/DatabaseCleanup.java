package kitchenpos.utils;

import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
@ActiveProfiles("test")
public class DatabaseCleanup {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        List<Object[]> tables = entityManager.createNativeQuery("SHOW TABLES")
                .getResultList();

        for (Object[] table : tables) {
            String tableName = (String) table[0];
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
