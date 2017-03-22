package swt6.spring.worklog.dao;

import java.util.List;

public interface GenericDao<T, ID> {
	void save(T entity);
	T merge(T entity);
	T findById(ID id);
	List<T> findAll();

}
