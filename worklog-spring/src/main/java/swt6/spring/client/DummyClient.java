package swt6.spring.client;

import java.util.Date;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import swt6.spring.dao.EmployeeRepository;
import swt6.spring.dao.IssueRepository;
import swt6.spring.dao.LogbookEntryRepository;
import swt6.spring.dao.ModuleRepository;
import swt6.spring.dao.ProjectRepository;
import swt6.spring.domain.Address;
import swt6.spring.domain.Employee;
import swt6.spring.domain.LogbookEntry;
import swt6.spring.domain.Module;
import swt6.spring.domain.PermanentEmployee;
import swt6.spring.domain.Project;
import swt6.spring.logic.IssueTrackingDal;

public class DummyClient {

	public static void main(String[] args) {
		try (AbstractApplicationContext factory = new ClassPathXmlApplicationContext(
				"swt6/spring/applicationContext.xml")) {
			EmployeeRepository emplRepo = factory.getBean("emplRepo", EmployeeRepository.class);
			LogbookEntryRepository lbRepo = factory.getBean("lbRepo", LogbookEntryRepository.class);
			ModuleRepository moduleRepo = factory.getBean("moduleRepo", ModuleRepository.class);
			ProjectRepository projectRepo = factory.getBean("projectRepo", ProjectRepository.class);
			IssueRepository issueRepo = factory.getBean("issueRepo", IssueRepository.class);

			IssueTrackingDal dal = factory.getBean("issueDal", IssueTrackingDal.class);

			Employee empl1 = new PermanentEmployee("some", "name", new Date(), new Address("4300", "sdas", "1231"),
					20000);
			empl1 = emplRepo.save(empl1);
			Module module = moduleRepo.save(new Module("m1"));
			Project project = projectRepo.save(new Project("project 1", empl1));

			LogbookEntry lb = new LogbookEntry();
			lb.setStartTime(new Date());
			lb.setModule(module);
			lb.setEmployee(emplRepo.findOne(1L));

			lb = lbRepo.save(lb);

			lb = new LogbookEntry();
			lb.setStartTime(new Date());
			lb.setModule(moduleRepo.findAll().get(0));
			lb.setEmployee(emplRepo.findOne(1L));

			lb = lbRepo.save(lb);

			emplRepo.findAll().forEach(System.out::println);
			dal.findAllEmployees();

			emplRepo.findByLastNameContaining("na").forEach(System.out::println);
			;

			lb = lbRepo.save(lb);

			lbRepo.findAll().forEach(lbRepo::delete);

//			projectRepo.findForProjectLead(empl1).forEach(projectRepo::delete);

			dal.deleteEmployee(empl1);

		}

	}

}
