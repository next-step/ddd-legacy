package kitchenpos.dao.jdbc.template;

import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.model.OrderLineItem;
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
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTemplateOrderLineItemDao implements OrderLineItemDao {
    private static final String TABLE_NAME = "order_line_item";
    private static final String KEY_COLUMN_NAME = "seq";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public JdbcTemplateOrderLineItemDao(final DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(KEY_COLUMN_NAME)
        ;
    }

    @Override
    public OrderLineItem save(final OrderLineItem entity) {
        final SqlParameterSource parameters = new BeanPropertySqlParameterSource(entity);
        final Number key = jdbcInsert.executeAndReturnKey(parameters);
        return select(key.longValue());
    }

    @Override
    public Optional<OrderLineItem> findById(final Long id) {
        try {
            return Optional.of(select(id));
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<OrderLineItem> findAll() {
        final String sql = "SELECT seq, order_id, menu_id, quantity FROM order_line_item";
        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> toEntity(resultSet));
    }

    @Override
    public List<OrderLineItem> findAllByOrderId(final Long orderId) {
        final String sql = "SELECT seq, order_id, menu_id, quantity FROM order_line_item WHERE order_id = (:orderId)";
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("orderId", orderId);
        return jdbcTemplate.query(sql, parameters, (resultSet, rowNumber) -> toEntity(resultSet));
    }

    private OrderLineItem select(final Long id) {
        final String sql = "SELECT seq, order_id, menu_id, quantity FROM order_line_item WHERE seq = (:seq)";
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("seq", id);
        return jdbcTemplate.queryForObject(sql, parameters, (resultSet, rowNumber) -> toEntity(resultSet));
    }

    private OrderLineItem toEntity(final ResultSet resultSet) throws SQLException {
        final OrderLineItem entity = new OrderLineItem();
        entity.setSeq(resultSet.getLong(KEY_COLUMN_NAME));
        entity.setOrderId(resultSet.getLong("order_id"));
        entity.setMenuId(resultSet.getLong("menu_id"));
        entity.setQuantity(resultSet.getLong("quantity"));
        return entity;
    }
}
