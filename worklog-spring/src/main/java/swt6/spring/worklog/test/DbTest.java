package swt6.spring.worklog.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import swt6.spring.worklog.dao.EmployeeDao;
import swt6.spring.worklog.dao.EmployeeRepository;
import swt6.spring.worklog.dao.LogbookEntryDao;
import swt6.spring.worklog.dao.LogbookEntryRepository;
import swt6.spring.worklog.domain.Employee;
import swt6.spring.worklog.domain.LogbookEntry;
import swt6.util.DateUtil;
import swt6.util.DbScriptRunner;
import swt6.util.JpaUtil;

public class DbTest {
	private static void createSchema(DataSource ds, String ddlScript) {
		try {
			DbScriptRunner scriptRunner = new DbScriptRunner(ds.getConnection());
			InputStream is = DbTest.class.getClassLoader().getResourceAsStream(ddlScript);
			if (is == null)
				throw new IllegalArgumentException(String.format("File %s not found in classpath.", ddlScript));
			scriptRunner.runScript(new InputStreamReader(is));
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			return;
		}
	}

	private static void testJdbc() {

		try (AbstractApplicationContext factory = new ClassPathXmlApplicationContext(
				"swt6/spring/worklog/applicationContext-jdbc.xml")) {

			System.out.println("----------------- create schema ----------------- ");
			createSchema(factory.getBean("dataSource", DataSource.class),
					"swt6/spring/worklog/CreateWorklogDbSchema.sql");

			// get reference to implementation of EmployeeDao
			EmployeeDao emplDao = factory.getBean("emplDaoJdbc", EmployeeDao.class);
			System.out.println("----------------- save employee ----------------- ");
			Employee empl1 = new Employee("Bill", "Gates", DateUtil.getDate(1970, 10, 26));

			emplDao.save(empl1);

			empl1.setFirstName("William Henry");
			emplDao.save(empl1);

			System.out.println("----------------- find employee ----------------- ");
			Employee empl = emplDao.findById(1L);
			System.out.println("empl=" + (empl == null ? (null) : empl.toString()));

			empl = emplDao.findById(100L);
			System.out.println("empl=" + (empl == null ? (null) : empl.toString()));

			System.out.println("----------------- find all employees ----------------- ");
			for (Employee e : emplDao.findAll())
				System.out.println(e);
		}
	}

	private static void testJpa() {
		try (AbstractApplicationContext factory = new ClassPathXmlApplicationContext(
				"swt6/spring/worklog/applicationContext-jpa1.xml")) {

			EntityManagerFactory emFactory = factory.getBean("emFactory", EntityManagerFactory.class);

			EmployeeDao emplDao = factory.getBean("emplDaoJpa", EmployeeDao.class);

			System.out.println("----------------- save employee ----------------- ");

			Employee empl1 = new Employee("Josef", "Himmelbauer", DateUtil.getDate(1950, 1, 1));

			JpaUtil.beginTransaction(emFactory);
			emplDao.save(empl1);

			System.out.println("----------------- update employee ----------------- ");

			empl1.setFirstName("Kevin");
			empl1 = emplDao.merge(empl1);

			Long id = empl1.getId();
			JpaUtil.commitTransaction(emFactory);

			System.out.println("----------------- find employee ----------------- ");
			JpaUtil.beginTransaction(emFactory);

			Employee empl = emplDao.findById(id);
			System.out.println("empl=" + (empl == null ? (null) : empl.toString()));

			empl = emplDao.findById(100L);
			System.out.println("empl=" + (empl == null ? (null) : empl.toString()));

			System.out.println("----------------- find all employees ----------------- ");

			System.out.println("findAll");
			for (Employee e : emplDao.findAll())
				System.out.println(e);

			JpaUtil.commitTransaction(emFactory);

			// -----------------------------------------------------------------------

			LogbookEntryDao entryDao = factory.getBean("entryDaoJpa", LogbookEntryDao.class);

			System.out.println("----------------- save logbook entries ----------------- ");
			JpaUtil.beginTransaction(emFactory);

			LogbookEntry entry1 = new LogbookEntry("Analyse", DateUtil.getTime(8, 30), DateUtil.getTime(16, 15));
			entry1.setEmployee(empl1);
			entryDao.save(entry1);

			LogbookEntry entry2 = new LogbookEntry("Implementierung", DateUtil.getTime(8, 0), DateUtil.getTime(17, 30));
			entry2.setEmployee(new Employee("Valentino", "Hummelbauer", DateUtil.getDate(1940, 12, 24)));
			entryDao.save(entry2);

			JpaUtil.commitTransaction(emFactory);

			System.out.println("----------------- find all logbook entries ----------------- ");
			JpaUtil.beginTransaction(emFactory);

			for (LogbookEntry e : entryDao.findAll()) {
				System.out.println(e);
			}
			//
			JpaUtil.commitTransaction(emFactory);
			// ----------------------------------------------------------------------------------
		}
	}

	private static void testSpringData() {
		try (AbstractApplicationContext factory = new ClassPathXmlApplicationContext(
				"swt6/spring/worklog/applicationContext-jpa1.xml")) {

			EntityManagerFactory emFactory = factory.getBean("emFactory", EntityManagerFactory.class);
			JpaUtil.executeInTransaction(emFactory, () -> {
				EmployeeRepository emplRepository = JpaUtil.getJpaRepository(emFactory, EmployeeRepository.class);

				System.out.println("----------------- save employee ----------------- ");

				Employee empl1 = new Employee("Josef", "Himmelbauer", DateUtil.getDate(1950, 1, 1));

				emplRepository.save(empl1);

				System.out.println("----------------- update employee ----------------- ");

				empl1.setFirstName("Kevin");
				empl1 = emplRepository.save(empl1);

				Long id = empl1.getId();
				emplRepository.flush();
				System.out.println("----------------- find employee ----------------- ");

				Employee empl = emplRepository.findOne(id);
				System.out.println("empl=" + (empl == null ? (null) : empl.toString()));

				empl = emplRepository.findOne(100L);
				System.out.println("empl=" + (empl == null ? (null) : empl.toString()));

				System.out.println("----------------- find all employees ----------------- ");

				System.out.println("findAll");
				for (Employee e : emplRepository.findAll())
					System.out.println(e);


				// -----------------------------------------------------------------------

				LogbookEntryRepository entryRepo = JpaUtil.getJpaRepository(emFactory, LogbookEntryRepository.class);

				System.out.println("----------------- save logbook entries ----------------- ");

				LogbookEntry entry1 = new LogbookEntry("Analyse", DateUtil.getTime(8, 30), DateUtil.getTime(16, 15));
				entry1.setEmployee(empl1);
				entryRepo.save(entry1);

				LogbookEntry entry2 = new LogbookEntry("Implementierung", DateUtil.getTime(8, 0), DateUtil.getTime(17, 30));
				entry2.setEmployee(new Employee("Valentino", "Hummelbauer", DateUtil.getDate(1940, 12, 24)));
				entryRepo.save(entry2);


				System.out.println("----------------- find all logbook entries ----------------- ");

				for (LogbookEntry e : entryRepo.findAll()) {
					System.out.println(e);
				}
				//
			});
			
			// ----------------------------------------------------------------------------------
		}
	}

	public static void main(String[] args) {

		System.out.println("===========================================================");
		System.out.println("======================== testJDBC =========================");
		System.out.println("===========================================================");
		testJdbc();

		System.out.println("===========================================================");
		System.out.println("======================== testJPA  =========================");
		System.out.println("===========================================================");
		testJpa();

		System.out.println("===========================================================");
		System.out.println("======================== testSpringCode ===================");
		System.out.println("===========================================================");
		testSpringData();

		// System.out.println("===========================================================");
		// System.out.println("====================== testJPA
		// ============================");
		// System.out.println("===========================================================");
		// testJPA();
	}
}
