package swt6.spring.basics.aop.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component("workLog") // make advice detectable by context:component-scan
public class WorkLogImpl implements WorkLogFacade {

	private Map<Long, Employee> employees = new HashMap<>();

	private void init() {
		employees.put(1L, new Employee(1L, "Bill", "Gates"));
		employees.put(2L, new Employee(2L, "James", "Goslin"));
		employees.put(3L, new Employee(3L, "Bjarne", "Stroustrup"));
	}

	public WorkLogImpl() {
		init();
	}

	public Employee findEmployeeById(Long id) throws EmployeeIdNotFoundException {
		// try { Thread.sleep(30); } catch(Exception e) {}
		if (employees.get(id) == null)
			throw new EmployeeIdNotFoundException();
		return employees.get(id);
	}

	public List<Employee> findAllEmployees() {
		// try { Thread.sleep(50); } catch(Exception e) {}
		return new ArrayList<Employee>(employees.values());
	}
}
