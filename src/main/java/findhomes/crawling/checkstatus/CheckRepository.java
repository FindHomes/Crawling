package findhomes.crawling.checkstatus;

import findhomes.crawling.checkstatus.domain.HouseForCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
@Repository
public class CheckRepository {

    private final NamedParameterJdbcTemplate template;

    public CheckRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<HouseForCheck> findAll() {
        String sql = "select house_id, url, status, checked_at from houses_tbl";
        return template.query(sql, houseRowMapper());
    }

    public List<HouseForCheck> findByPage(int page, int limit) {
        String sql = "select house_id, url, status, checked_at " +
                "from houses_tbl " +
                "order by house_id " +
                "limit " + limit + " offset " + page * limit;
        return template.query(sql, houseRowMapper());
    }

    public void update(HouseForCheck house) {
        String sql = "update houses_tbl set status=:status, checked_at=:checkedAt where house_id=:houseId";
        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(house);
        template.update(sql, param);
    }

    private RowMapper<HouseForCheck> houseRowMapper() {
        return BeanPropertyRowMapper.newInstance(HouseForCheck.class);
    }
}
