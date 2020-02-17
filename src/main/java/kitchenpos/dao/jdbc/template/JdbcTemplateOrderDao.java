package kitchenpos.dao.jdbc.template;

import kitchenpos.dao.OrderDao;
import kitchenpos.model.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JdbcTemplateOrderDao implements OrderDao {
    private static final String TABLE_NAME = "orders";
    private static final String KEY_COLUMN_NAME = "id";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public JdbcTemplateOrderDao(final DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(KEY_COLUMN_NAME)
        ;
    }

    @Override
    public Order save(final Order entity) {
        if (Objects.isNull(entity.getId())) {
            final SqlParameterSource parameters = new BeanPropertySqlParameterSource(entity);
            final Number key = jdbcInsert.executeAndReturnKey(parameters);
            return select(key.longValue());
        }
        update(entity);
        return entity;
    }

    @Override
    public Optional<Order> findById(final Long id) {
        try {
            return Optional.of(select(id));
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Order> findAll() {
        final String sql = "SELECT id, order_table_id, order_status, ordered_time FROM orders";
        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> toEntity(resultSet));
    }

    @Override
    public boolean existsByOrderTableIdAndOrderStatusIn(final Long orderTableId, final List<String> orderStatuses) {
        final String sql = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END" +
                " FROM orders WHERE order_table_id = (:orderTableId) AND order_status IN (:orderStatuses)";
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("orderTableId", orderTableId)
                .addValue("orderStatuses", orderStatuses);
        return jdbcTemplate.queryForObject(sql, parameters, Boolean.class);
    }

    @Override
    public boolean existsByOrderTableIdInAndOrderStatusIn(final List<Long> orderTableIds, final List<String> orderStatuses) {
        final String sql = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END" +
                " FROM orders WHERE order_table_id IN (:orderTableIds) AND order_status IN (:orderStatuses)";
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("orderTableIds", orderTableIds)
                .addValue("orderStatuses", orderStatuses);
        return jdbcTemplate.queryForObject(sql, parameters, Boolean.class);
    }

    private Order select(final Long id) {
        final String sql = "SELECT id, order_table_id, order_status, ordered_time FROM orders WHERE id = (:id)";
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id);
        return jdbcTemplate.queryForObject(sql, parameters, (resultSet, rowNumber) -> toEntity(resultSet));
    }

    private void update(final Order entity) {
        final String sql = "UPDATE orders SET order_status = (:orderStatus) WHERE id = (:id)";
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("orderStatus", entity.getOrderStatus())
                .addValue("id", entity.getId());
        jdbcTemplate.update(sql, parameters);
    }

    private Order toEntity(final ResultSet resultSet) throws SQLException {
        final Order entity = new Order();
        entity.setId(resultSet.getLong(KEY_COLUMN_NAME));
        entity.setOrderTableId(resultSet.getLong("order_table_id"));
        entity.setOrderStatus(resultSet.getString("order_status"));
        entity.setOrderedTime(resultSet.getObject("ordered_time", LocalDateTime.class));
        return entity;
    }
}
