package swt6.spring.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyStore.Entry;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import swt6.spring.domain.Address;
import swt6.spring.domain.Employee;
import swt6.spring.domain.Issue;
import swt6.spring.domain.IssueType;
import swt6.spring.domain.LogbookEntry;
import swt6.spring.domain.PermanentEmployee;
import swt6.spring.domain.Phase;
import swt6.spring.domain.PhaseDescriptor;
import swt6.spring.domain.PriorityType;
import swt6.spring.domain.Project;
import swt6.spring.domain.TemporaryEmployee;
import swt6.spring.logic.IssueTrackingDal;

public class InteractiveClient {

	private static final String configFile = "swt6/spring/applicationContext.xml";

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
			WorkLogFacade facade = appCtx.getBean("workLog", WorkLogFacade.class);
			init(dfmt, facade);

			System.out.println("Hibernate Employee Admin");
			System.out.println(availCmds);
			String userCmd = promptFor(in, "");
			while (!userCmd.equals("quit")) {
				switch (userCmd.toLowerCase().trim()) {
				case "insert p employee":
					try {
						facade.saveEmployee(new PermanentEmployee(promptFor(in, "firstName"), promptFor(in, "lastName"),
								dfmt.parse(promptFor(in, "dob (dd.mm.yyyy)")),
								new Address(promptFor(in, "zipCode"), promptFor(in, "city"), promptFor(in, "street")),
								Integer.parseInt(promptFor(in, "salary"))));

					} catch (ParseException e) {

					}
					userCmd = promptFor(in, "");
					break;

				case "insert t employee":
					try {
						facade.saveEmployee(new TemporaryEmployee(promptFor(in, "firstName"), promptFor(in, "lastName"),
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

					facade.listEmployees(strId);

					userCmd = promptFor(in, "");
					break;

				case "list project":
					strId = promptFor(in, "id (no input lists all)");
					facade.listProjects(strId);

					userCmd = promptFor(in, "");
					break;

				case "list project employee":
					strId = promptFor(in, "id");
					facade.listEmployeesOfProject(Long.parseLong(strId));

					userCmd = promptFor(in, "");
					break;

				case "add employee project":
					Long employeeId = Long.parseLong(promptFor(in, "employeeId"));
					Long projectId = Long.parseLong(promptFor(in, "projectId"));

					facade.addEmployeeToProject(employeeId, projectId);

					userCmd = promptFor(in, "");
					break;

				case "remove employee project":
					employeeId = Long.parseLong(promptFor(in, "employeeId"));
					projectId = Long.parseLong(promptFor(in, "projectId"));

					facade.removeEmployeeFromProject(employeeId, projectId);

					userCmd = promptFor(in, "");
					break;

				case "insert project":
					Project p = new Project(promptFor(in, "Project name"));
					Employee lead = facade
							.findEmployeeForId(Long.parseLong(promptFor(in, "Employee Id for projectlead")));
					p.setProjectLeader(lead);
					facade.saveProject(p);

					userCmd = promptFor(in, "");
					break;

				case "insert issue":
					strId = promptFor(in, "projectId for Issueassignment");
					p = facade.findProjectForId(Long.parseLong(strId));
					Issue issue = new Issue(p);

					strId = promptFor(in, "Priortiy (LOW, NORMAL, HIGH)");

					issue.setPriority(strId.equals("") ? PriorityType.NORMAL : PriorityType.valueOf(strId));

					strId = promptFor(in, "issueState (NEW, OPEN, RESOLVED, CLOSED, REJECTED)");

					issue.setState(strId.equals("") ? IssueType.NEW : IssueType.valueOf(strId));

					strId = promptFor(in, "employeeId for Issue (optional)");
					if (!strId.equals(""))
						facade.assignEmployeToIssue(facade.findEmployeeForId(Long.parseLong(strId)), issue);
					else
						facade.saveIssue(issue);
					userCmd = promptFor(in, "");
					break;

				case "update issue":
					strId = promptFor(in, "issueId");
					issue = facade.findIssueForId(Long.parseLong(strId));

					strId = promptFor(in, "Priortiy (LOW, NORMAL, HIGH)");
					issue.setPriority(strId.equals("") ? PriorityType.NORMAL : PriorityType.valueOf(strId));

					strId = promptFor(in, "issueState (NEW, OPEN, RESOLVED, CLOSED, REJECTED)");
					issue.setState(strId.equals("") ? IssueType.NEW : IssueType.valueOf(strId));

					facade.saveIssue(issue);
					
					userCmd = promptFor(in, "");
					break;

				case "assign issue":
					strId = promptFor(in, "issueId");
					issue = facade.findIssueForId(Long.parseLong(strId));

					strId = promptFor(in, "employeeId for Issueassignemt");
					Employee e = facade.findEmployeeForId(Long.parseLong(strId));

					facade.assignEmployeToIssue(e, issue);

					userCmd = promptFor(in, "");
					break;

				case "insert entry":

					try {
						LogbookEntry entry = new LogbookEntry(dfmt.parse(promptFor(in, "start Date")),
								dfmt.parse(promptFor(in, "end date")));
						entry.setEmployee(
								facade.findEmployeeForId(Long.parseLong(promptFor(in, "assign employee (id)"))));
						entry.setPhase(new Phase(PhaseDescriptor.valueOf(
								promptFor(in, "assign Phase (ANALYSIS, IMPLEMENTATION, TEST, MAINTENANCE, OTHER"))));
						strId = promptFor(in, "issueId (blank for no assignemnt");

						if (!strId.equals("")) {
							issue = facade.findIssueForId(Long.parseLong(strId));
							facade.assignIssueToLogbookEntry(issue, entry);
						} else {
							strId = promptFor(in, "projectId");
							p = facade.findProjectForId(Long.parseLong(strId));
							while (p == null) {
								strId = promptFor(in, "invalid project id!\nTry again");
								p = facade.findProjectForId(Long.parseLong(strId));
							}
							facade.assignIssueToProject(entry, p);
						}

					} catch (ParseException e1) {
					}

					userCmd = promptFor(in, "");
					break;

				case "update entry":
					Long id = Long.parseLong(promptFor(in, "id"));
					LogbookEntry entry = facade.findLogbookEntryForId(id);

					entry.setEmployee(facade.findEmployeeForId(Long.parseLong(promptFor(in, "new employeeid"))));
					entry.setPhase(new Phase(PhaseDescriptor.valueOf(promptFor(in, "new Phase"))));
					strId = promptFor(in, "issueId (blank for no assignemnt");
					// Long id;
					if (!strId.equals("")) {
						issue = facade.findIssueForId(Long.parseLong(strId));
						facade.assignIssueToLogbookEntry(issue, entry);
					} else {
						strId = promptFor(in, "projectId");
						facade.assignIssueToProject(entry, facade.findProjectForId(Long.parseLong(strId)));
					}
					userCmd = promptFor(in, "");
					break;

				case "list entry":
					strId = promptFor(in, "id (no input lists all)");
					facade.listEntries(strId);
					userCmd = promptFor(in, "");
					break;

				case "list issues project":
					strId = promptFor(in, "projectId");
					String issueState = promptFor(in, "issueState (leave blank for all)");
					IssueType type = null;
					if (!issueState.equals(""))
						type = IssueType.valueOf(issueState);
					facade.listIssuesForProject(Long.parseLong(strId), type);

					userCmd = promptFor(in, "");
					break;

				case "list issue project by employee":
					strId = promptFor(in, "id");
					type = null;
					issueState = promptFor(in, "issuetype (blank for all)");

					if (!issueState.equals(""))
						type = IssueType.valueOf(issueState);

					facade.listIssuesOfProjectByEmployee(Long.parseLong(strId), type);
					userCmd = promptFor(in, "");
					break;

				case "list worktime project":
					userCmd = promptFor(in, "");
					// TODO 
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

	// private static void listProjects(String strId) {
	// if (strId.equals(""))
	// dal.findAllProjects().forEach(System.out::println);
	// else {
	// Long id;
	// try {
	// id = Long.parseLong(strId);
	// } catch (NumberFormatException e) {
	// System.err.println("invalid id" + strId);
	// return;
	// }
	// System.out.println(dal.findProjectById(id));
	// }
	// }
	//
	// private static void addEmployeeToProject(Long employeeId, Long projectId)
	// {
	// Employee e = dal.findEmployeeById(employeeId);
	// Project p = dal.findProjectById(projectId);
	// if (e == null) {
	// System.err.println("no employee with id" + employeeId);
	// return;
	// }
	// if (p == null) {
	// System.err.println("no employee with id" + projectId);
	// return;
	// }
	// dal.assignEmployeeToProject(e, p);
	//
	// System.out.println("employees working on " + p + ":");
	// p.getMembers().forEach(System.out::println);
	//
	// }
	//
	private static void init(DateFormat dfmt, WorkLogFacade dal) {
		try {
			Employee empl1 = dal.saveEmployee(new PermanentEmployee("Jack", "black", dfmt.parse("01.01.1993"),
					new Address("4300", "sdas", "1231"), 20000));

			dal.saveEmployee(new PermanentEmployee("Jane", "Corsair", dfmt.parse("01.01.1993"),
					new Address("4300", "sdas", "1231"), 27000));

			dal.saveEmployee(new PermanentEmployee("Lucky", "Bolero", dfmt.parse("01.01.1989"),
					new Address("4300", "sdas", "1231"), 10000));

			dal.saveEmployee(new PermanentEmployee("Daniel", "Raptor", dfmt.parse("01.01.1983"),
					new Address("4300", "sdas", "1231"), 30000));

			Project p = dal.saveProject(new Project("Project Orange", empl1));
			Issue i = dal.saveIssue(new Issue(p));

		} catch (ParseException e1) {
		}
	}
	//
	// private static void listIssuesForProject(Long id) {
	// dal.findAllIssuesForPoject(dal.findProjectById(id)).forEach(System.out::println);
	// }
	//
	// private static void listEmployees(String strId) {
	// if (strId.equals(""))
	// dal.findAllEmployees().forEach(System.out::println);
	// else {
	// Long id;
	// try {
	// id = Long.parseLong(strId);
	// } catch (NumberFormatException e) {
	// System.err.println("invalid id" + strId);
	// return;
	// }
	// System.out.println(dal.findEmployeeById(id));
	// }
	//
	// }
	//
	// private static void saveEmployee(Employee employee) {
	// dal.syncEmployee(employee);
	// }

}
