package swt6.spring.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import swt6.spring.domain.Address;
import swt6.spring.domain.Employee;
import swt6.spring.domain.Issue;
import swt6.spring.domain.PermanentEmployee;
import swt6.spring.domain.Project;
import swt6.spring.domain.TemporaryEmployee;
import swt6.spring.logic.IssueTrackingDal;

public class InteractiveClient {

	private static final String configFile = "swt6/spring/applicationContext.xml";

	private static IssueTrackingDal dal;

	static String promptFor(BufferedReader in, String p) {
		System.out.print(p + "> ");
		try {
			return in.readLine();
		} catch (Exception e) {
			return promptFor(in, p);
		}
	}

	public static void main(String[] args) {
		DateFormat dfmt = new SimpleDateFormat("dd.MM.yyyy");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String availCmds = "======= Issue Tracking =======\ncommands: insert p employee, insert t employee, list employee, insert project, list project, \n"
				+ "add employee project, remove employee project, insert issue, update issue, insert entry, \n"
				+ "update entry, list issues project, list worktime project, help, quit";

		try (AbstractApplicationContext appCtx = new ClassPathXmlApplicationContext(configFile)) {

			init(dfmt, appCtx);

			System.out.println("Hibernate Employee Admin");
			System.out.println(availCmds);
			String userCmd = promptFor(in, "");
			while (!userCmd.equals("quit")) {
				switch (userCmd) {
				case "insert p employee":
					try {
						saveEmployee(new PermanentEmployee(promptFor(in, "firstName"), promptFor(in, "lastName"),
								dfmt.parse(promptFor(in, "dob (dd.mm.yyyy)")),
								new Address(promptFor(in, "zipCode"), promptFor(in, "city"), promptFor(in, "street")),
								Integer.parseInt(promptFor(in, "salary"))));

					} catch (ParseException e) {

					}
					userCmd = promptFor(in, "");
					break;

				case "insert t employee":
					try {
						saveEmployee(new TemporaryEmployee(promptFor(in, "firstName"), promptFor(in, "lastName"),
								dfmt.parse(promptFor(in, "dob (dd.mm.yyyy)")),
								new Address(promptFor(in, "zipCode"), promptFor(in, "city"), promptFor(in, "street")),
								promptFor(in, "renter"), Integer.parseInt(promptFor(in, "rate")),
								dfmt.parse(promptFor(in, "start date")), dfmt.parse(promptFor(in, "end Date"))));

					} catch (ParseException e) {

					}
					userCmd = promptFor(in, "");
					break;

				case "list employee":
					String strId = promptFor(in, "id (no input lists all)");

					listEmployees(strId);

					userCmd = promptFor(in, "");
					break;

				case "list project":
					strId = promptFor(in, "id (no input lists all)");
					listProjects(strId);

					userCmd = promptFor(in, "");
					break;

				case "add employee project":
					Long employeeId = Long.parseLong(promptFor(in, "employeeId"));
					Long projectId = Long.parseLong(promptFor(in, "projectId"));

					addEmployeeToProject(employeeId, projectId);

					userCmd = promptFor(in, "");
					break;

				case "remove employee project":
					userCmd = promptFor(in, "");
					break;

				case "insert project":

					userCmd = promptFor(in, "");
					break;

				case "insert issue":

					userCmd = promptFor(in, "");
					break;

				case "update issue":

					userCmd = promptFor(in, "");
					break;

				case "insert entry":
					userCmd = promptFor(in, "");
					break;

				case "update entry":

					userCmd = promptFor(in, "");
					break;

				case "list issues project":
					listIssuesForProject(Long.parseLong(promptFor(in, "project id")));

					userCmd = promptFor(in, "");
					break;

				case "list worktime project":
					userCmd = promptFor(in, "");
					break;

				case "help":
					System.out.println(availCmds);
					userCmd = promptFor(in, "");
					break;

				default:
					System.err.println("Error: Invalid Command!");
					userCmd = promptFor(in, "");
					break;
				}
			}

		}
	}

	private static void listProjects(String strId) {
		if (strId.equals(""))
			dal.findAllProjects().forEach(System.out::println);
		else {
			Long id;
			try {
				id = Long.parseLong(strId);
			} catch (NumberFormatException e) {
				System.err.println("invalid id" + strId);
				return;
			}
			System.out.println(dal.findProjectById(id));
		}
	}

	private static void addEmployeeToProject(Long employeeId, Long projectId) {
		Employee e = dal.findEmployeeById(employeeId);
		Project p = dal.findProjectById(projectId);
		if (e == null) {
			System.err.println("no employee with id" + employeeId);
			return;
		}
		if (p == null) {
			System.err.println("no employee with id" + projectId);
			return;
		}
		dal.assignEmployeeToProject(e, p);

		System.out.println("employees working on " + p + ":");
		p.getMembers().forEach(System.out::println);

	}

	private static void init(DateFormat dfmt, AbstractApplicationContext appCtx) {
		dal = appCtx.getBean(IssueTrackingDal.class);
		try {
			Employee empl1 = dal.syncEmployee(new PermanentEmployee("Jack", "black", dfmt.parse("01.01.1993"),
					new Address("4300", "sdas", "1231"), 20000));

			dal.syncEmployee(new PermanentEmployee("Jane", "Corsair", dfmt.parse("01.01.1993"),
					new Address("4300", "sdas", "1231"), 27000));

			dal.syncEmployee(new PermanentEmployee("Lucky", "Bolero", dfmt.parse("01.01.1989"),
					new Address("4300", "sdas", "1231"), 10000));

			dal.syncEmployee(new PermanentEmployee("Daniel", "Raptor", dfmt.parse("01.01.1983"),
					new Address("4300", "sdas", "1231"), 30000));

			Project p = dal.syncProject(new Project("Project Orange", empl1));
			Issue i = dal.syncIssue(new Issue(p));

		} catch (ParseException e1) {
		}
	}

	private static void listIssuesForProject(Long id) {
		dal.findAllIssuesForPoject(dal.findProjectById(id)).forEach(System.out::println);
	}

	private static void listEmployees(String strId) {
		if (strId.equals(""))
			dal.findAllEmployees().forEach(System.out::println);
		else {
			Long id;
			try {
				id = Long.parseLong(strId);
			} catch (NumberFormatException e) {
				System.err.println("invalid id" + strId);
				return;
			}
			System.out.println(dal.findEmployeeById(id));
		}

	}

	private static void saveEmployee(Employee employee) {
		dal.syncEmployee(employee);
	}

}
