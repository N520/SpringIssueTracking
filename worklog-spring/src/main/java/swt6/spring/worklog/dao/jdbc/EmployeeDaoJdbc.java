package swt6.spring.worklog.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import swt6.spring.worklog.dao.EmployeeDao;
import swt6.spring.worklog.domain.Employee;
import swt6.util.DateUtil;

public class EmployeeDaoJdbc extends JdbcDaoSupport implements EmployeeDao {
	// version 1: Data access code without spring
	// not suitable if generated key should be extracted.
	public void save1(final Employee e) throws DataAccessException {
		final String sql = "insert into Employee (firstName, lastName, dateOfBirth) " + "values (?, ?, ?)";
		try (Connection conn = getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, e.getFirstName());
			stmt.setString(2, e.getLastName());
			stmt.setDate(3, DateUtil.toSqlDate(e.getDateOfBirth()));
			stmt.executeUpdate();
		} catch (SQLException ex) {
			System.err.println(ex);
		}
	}

	// Version 2 usin jdbc Template lambda expr
	// not suitable if you need generated key

	public void save2(Employee e) {
		final String sql = "insert into Employee (firstName, lastName, dateOfBirth) " + "values (?, ?, ?)";
		getJdbcTemplate().update(sql, ps -> {
			ps.setString(1, e.getFirstName());
			ps.setString(2, e.getLastName());
			ps.setDate(3, DateUtil.toSqlDate(e.getDateOfBirth()));
		});
		// Object[] params = new Object[] { e.getFirstName(), e.getLastName(),
		// DateUtil.toSqlDate(e.getDateOfBirth()) };
		// getJdbcTemplate().update(sql, params);

	}

	// Version 3 usin jdbc Template
	@Override
	public void save(Employee e) {
		final String sql = "insert into Employee (firstName, lastName, dateOfBirth) " + "values (?, ?, ?)";

		PreparedStatementCreator psc = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, new String[] { "id" });
				ps.setString(1, e.getFirstName());
				ps.setString(2, e.getLastName());
				ps.setDate(3, DateUtil.toSqlDate(e.getDateOfBirth()));
				return ps;
			}
		};

		KeyHolder keyholder = new GeneratedKeyHolder();

		getJdbcTemplate().update(psc, keyholder);

		e.setId(keyholder.getKey().longValue());

		// Object[] params = new Object[] { e.getFirstName(), e.getLastName(),
		// DateUtil.toSqlDate(e.getDateOfBirth()) };
		// getJdbcTemplate().update(sql, params);

	}

	@Override
	public Employee merge(Employee entity) {
		save(entity);
		return entity;
	}

	// v1 process resultset using callbackHandler
	// @Override
	public Employee findById1(Long id) {
		final String sql = "select id, firstname, lastname, dateofbirth from employee where id = ?";
		final Object[] params = { id };
		Employee empl = new Employee();

		getJdbcTemplate().query(sql, resulSet -> {
			empl.setFirstName(resulSet.getString(2));
			empl.setLastName(resulSet.getString(3));
			empl.setDateOfBirth(resulSet.getDate(4));
			empl.setId((resulSet.getLong(1)));
		}, params);
		return empl.getId() == null ? null : empl;
	}

	// v2 process resultset with rowmapper
	public Employee findById(Long id) {
		final String sql = "select id, firstname, lastname, dateofbirth from employee where id = ?";
		final Object[] params = { id };
		Employee empl = new Employee();

		try {
			empl = getJdbcTemplate().queryForObject(sql, params, new EmployeeRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

		return empl.getId() == null ? null : empl;
	}

	@Override
	public List<Employee> findAll() {
		final String sql = "select id, firstname, lastname, dateofbirth from employee";
		return getJdbcTemplate().query(sql, new EmployeeRowMapper());
	}

	protected static class EmployeeRowMapper implements RowMapper<Employee> {

		@Override
		public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
			Employee empl = new Employee();
			empl.setFirstName(rs.getString(2));
			empl.setLastName(rs.getString(3));
			empl.setDateOfBirth(rs.getDate(4));
			empl.setId((rs.getLong(1)));
			return empl;

		}
	}
}
