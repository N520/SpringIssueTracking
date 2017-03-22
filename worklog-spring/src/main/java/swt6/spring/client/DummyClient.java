package swt6.spring.client;

import java.util.Date;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import swt6.spring.dao.EmployeeRepository;
import swt6.spring.dao.LogbookEntryRepository;
import swt6.spring.domain.Address;
import swt6.spring.domain.Employee;
import swt6.spring.domain.LogbookEntry;
import swt6.spring.domain.Module;
import swt6.spring.domain.PermanentEmployee;

public class DummyClient {

	public static void main(String[] args) {
		try (AbstractApplicationContext factory = new ClassPathXmlApplicationContext(
				"swt6/spring/applicationContext.xml")) {
			EmployeeRepository emplRepo = factory.getBean("emplRepo", EmployeeRepository.class);
			LogbookEntryRepository lbRepo = factory.getBean("lbRepo", LogbookEntryRepository.class);

			Employee empl1 = new PermanentEmployee("some", "name", new Date(), new Address("4300", "sdas", "1231"),
					20000);
			empl1 = emplRepo.save(empl1);

			LogbookEntry lb = new LogbookEntry();
			lb.setStartTime(new Date());
			lb.setModule(new Module("m1"));
			lb.setEmployee(emplRepo.findOne(1L));

			lb = lbRepo.save(lb);

			lb = new LogbookEntry();
			lb.setStartTime(new Date());
			lb.setModule(new Module("m1"));
			lb.setEmployee(emplRepo.findOne(1L));

			lb = lbRepo.save(lb);

			emplRepo.findAll().forEach(System.out::println);

			emplRepo.findByLastNameContaining("na").forEach(System.out::println);
			;

			lb = lbRepo.save(lb);

			lbRepo.findForEmployee(empl1.getId()).forEach(System.out::println);

		}

	}

}
