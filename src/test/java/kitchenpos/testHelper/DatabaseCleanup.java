package kitchenpos.testHelper;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import com.google.common.base.CaseFormat;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import javax.transaction.Transactional;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleanup implements InitializingBean {

    @PersistenceContext
    private EntityManager entityManager;

    private final Map<String, String> idColumnNamePerTable = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        List<EntityType<?>> entities = entityManager.getMetamodel()
            .getEntities()
            .stream()
            .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
            .collect(toList());

        for (EntityType<?> entity : entities) {
            String idColumnName = getIdColumnNameByEntity(entity);
            String tableName = getTableNameByEntity(entity);
            idColumnNamePerTable.put(tableName, idColumnName);
        }

    }

    private String getIdColumnNameByEntity(EntityType<?> e) {
        Field idField = getIdFieldByEntity(e);
        String name = idField.getName();
        Column column = idField.getAnnotation(Column.class);
        return column != null ? column.name() : name;
    }

    private String getTableNameByEntity(EntityType<?> e) {
        Table tableAnnotation = e.getJavaType().getAnnotation(Table.class);
        if (isEmpty(tableAnnotation)) {
            return getTableNameByClassName(e.getName());
        }

        return isEmpty(tableAnnotation.name()) ?
            getTableNameByClassName(e.getName()) : tableAnnotation.name();
    }


    private Field getIdFieldByEntity(EntityType<?> e) {
        try {
            return e.getJavaType().getDeclaredField("id");
        } catch (NoSuchFieldException ex) {
        }

        try {
            return e.getJavaType().getDeclaredField("seq");
        } catch (NoSuchFieldException ex) {
            throw new IllegalArgumentException(e.getName() + " Entity의 ID Field가 id / seq중 하나가 아닙니다");
        }
    }

    private String getTableNameByClassName(String e) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e);
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        for (Entry<String, String> tableInfo : idColumnNamePerTable.entrySet()) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableInfo.getKey()).executeUpdate();
        }

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
