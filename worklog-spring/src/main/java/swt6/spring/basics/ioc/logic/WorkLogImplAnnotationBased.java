package swt6.spring.basics.ioc.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import swt6.spring.basics.ioc.domain.Employee;
import swt6.spring.basics.ioc.util.Log;
import swt6.spring.basics.ioc.util.Logger;
import swt6.spring.basics.ioc.util.LoggerType;

@Component("workLog")
// @Named("workLog")
public class WorkLogImplAnnotationBased implements WorkLogFacade {
	private Map<Long, Employee> employees = new HashMap<Long, Employee>();

	// v1 Autowired from string
	 @Autowired(required = true)
	 @Log(value=LoggerType.FILE)
//	 v2 @Inject from JSR330(required = true)
	// v3
//	@Inject
//	@Named("consoleLoger")
	private Logger logger;

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public WorkLogImplAnnotationBased() {
		init();
	}

	private void init() {
		employees.put(1L, new Employee(1L, "Bill", "Gates"));
		employees.put(2L, new Employee(2L, "James", "Goslin"));
		employees.put(3L, new Employee(3L, "Bjarne", "Stroustrup"));
	}

	public WorkLogImplAnnotationBased(Logger logger) {
		this();
		this.logger = logger;
	}

	public Employee findEmployeeById(Long id) {
		logger.log("findEmployeById(" + id + ")");
		return employees.get(id);
	}

	public List<Employee> findAllEmployees() {
		logger.log("findAllEmployees()");
		return new ArrayList<Employee>(employees.values());
	}
}
