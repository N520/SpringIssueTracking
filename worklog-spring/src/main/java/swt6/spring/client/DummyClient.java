package swt6.spring.client;

import java.util.Date;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import swt6.spring.dao.EmployeeRepository;
import swt6.spring.domain.Address;
import swt6.spring.domain.PermanentEmployee;

public class DummyClient {

	public static void main(String[] args) {
		try (AbstractApplicationContext factory = new ClassPathXmlApplicationContext(
				"swt6/spring/applicationContext.xml")) {
			EmployeeRepository emplRepo = factory.getBean("emplRepo", EmployeeRepository.class);

			emplRepo.save(
					new PermanentEmployee("some", "name", new Date(), new Address("4300", "sdas", "1231"), 20000));

			emplRepo.findAll().forEach(System.out::println);
			
			emplRepo.findByLastNameContaining("na").forEach(System.out::println);;
		}

	}

}
