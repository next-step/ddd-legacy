package kitchenpos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.Type;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DatabaseCleaner {

    private final EntityManager entityManager;
    private final List<String> tableNames;

    /**
     * @param entityManager
     * JPA 엔티티 정보를 조회하여 테이블 이름 리스트 생성
     */
    public DatabaseCleaner(final EntityManager entityManager) {
        this.entityManager = entityManager;
        this.tableNames = entityManager.getMetamodel()
            .getEntities()
            .stream()
            .map(Type::getJavaType)
            .map(javaType -> javaType.getAnnotation(Table.class))
            .map(Table::name)
            .collect(Collectors.toList());
    }

    /**
     * execute(): 데이터베이스 초기화 (테이블 데이터 삭제)
     * Tip: `TRUNCATE`는 테이블의 모든 데이터를 삭제하며, 자동 증가(AUTO_INCREMENT) 값을 초기화하는 특징이 있음
     */
    @Transactional
    public void execute() {
        entityManager.flush(); // 변경 사항을 DB에 반영
        entityManager.createNativeQuery("SET foreign_key_checks = 0").executeUpdate(); // 외래 키 제약 해제

        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate(); // 테이블 데이터 삭제
        }

        entityManager.createNativeQuery("SET foreign_key_checks = 1").executeUpdate(); // 외래 키 제약 다시 활성화
    }
}
