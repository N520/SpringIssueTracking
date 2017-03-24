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
import swt6.util.DateUtil;

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
		DateFormat dfmt = new SimpleDateFormat("dd.MM.yyyy hh:mm");
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

					issue.setPriority(
							strId.equals("") ? PriorityType.NORMAL : PriorityType.valueOf(strId.toUpperCase()));

					strId = promptFor(in, "issueState (NEW, OPEN, RESOLVED, CLOSED, REJECTED)");

					issue.setState(strId.equals("") ? IssueType.NEW : IssueType.valueOf(strId.toUpperCase()));

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
					issue.setPriority(
							strId.equals("") ? PriorityType.NORMAL : PriorityType.valueOf(strId.toUpperCase()));

					strId = promptFor(in, "issueState (NEW, OPEN, RESOLVED, CLOSED, REJECTED)");
					issue.setState(strId.equals("") ? IssueType.NEW : IssueType.valueOf(strId.toUpperCase()));

					strId = promptFor(in, "effort");
					issue.setEffort(strId.equals("") ? issue.getEffort() : Integer.parseInt(strId));

					strId = promptFor(in, "estimatedTime");
					issue.setEstimatedTime(strId.equals("") ? issue.getEstimatedTime() : Integer.parseInt(strId));

					facade.saveIssue(issue);

					userCmd = promptFor(in, "");
					break;

				case "assign issue":
					strId = promptFor(in, "issueId");
					issue = facade.findIssueForId(Long.parseLong(strId));

					strId = promptFor(in, "employeeId for Issueassignemt");
					Employee e = facade.findEmployeeForId(Long.parseLong(strId));

					try {
						facade.assignEmployeToIssue(e, issue);
					} catch (IllegalStateException ex) {
						System.err.println(ex.getMessage());
					}

					userCmd = promptFor(in, "");
					break;

				case "insert entry":

					try {
						LogbookEntry entry = new LogbookEntry(dfmt.parse(promptFor(in, "start Date")),
								dfmt.parse(promptFor(in, "end date")));
						entry.setEmployee(
								facade.findEmployeeForId(Long.parseLong(promptFor(in, "assign employee (id)"))));
						entry.setPhase(new Phase(PhaseDescriptor.valueOf(
								promptFor(in, "assign Phase (ANALYSIS, IMPLEMENTATION, TEST, MAINTENANCE, OTHER")
										.toUpperCase())));
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
							facade.assignLogbookEntryToProject(entry, p);
						}

					} catch (ParseException e1) {
					}

					userCmd = promptFor(in, "");
					break;

				case "update entry":
					Long id = Long.parseLong(promptFor(in, "id"));
					LogbookEntry entry = facade.findLogbookEntryForId(id);

					entry.setEmployee(facade.findEmployeeForId(Long.parseLong(promptFor(in, "new employeeid"))));
					entry.setPhase(new Phase(PhaseDescriptor.valueOf(promptFor(in, "new Phase").toUpperCase())));
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

				case "list project issue":
					strId = promptFor(in, "projectId");
					String issueState = promptFor(in, "issueState (leave blank for all)");
					IssueType type = null;
					if (!issueState.equals(""))
						type = IssueType.valueOf(issueState.toUpperCase());
					facade.listIssuesForProject(Long.parseLong(strId), type);

					userCmd = promptFor(in, "");
					break;

				case "list issue project by employee":
					strId = promptFor(in, "id");
					type = null;
					issueState = promptFor(in, "issuetype (blank for all)");

					if (!issueState.equals(""))
						type = IssueType.valueOf(issueState.toUpperCase());

					facade.listIssuesOfProjectByEmployee(Long.parseLong(strId), type);
					userCmd = promptFor(in, "");
					break;

				case "list worktime project":
					strId = promptFor(in, "id");
					p = facade.findProjectForId(Long.parseLong(strId));

					facade.showWorktimeForProjectPerEmployee(p);

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

	private static void init(DateFormat dfmt, WorkLogFacade dal) {
		try {

			Employee empl1 = dal.saveEmployee(new PermanentEmployee("Jack", "black", dfmt.parse("01.01.1993 0:0"),
					new Address("4300", "sdas", "1231"), 20000));
			System.out.println("inserted empl1");

			dal.saveEmployee(new PermanentEmployee("Jane", "Corsair", dfmt.parse("01.01.1993 0:0"),
					new Address("4300", "sdas", "1231"), 27000));

			dal.saveEmployee(new PermanentEmployee("Lucky", "Bolero", dfmt.parse("01.01.1989 0:0"),
					new Address("4300", "sdas", "1231"), 10000));

			dal.saveEmployee(new PermanentEmployee("Daniel", "Raptor", dfmt.parse("01.01.1983 0:0"),
					new Address("4300", "sdas", "1231"), 30000));

			Project p = dal.saveProject(new Project("Project Orange", empl1));
			Issue i = dal.saveIssue(new Issue(p));
			LogbookEntry lb = new LogbookEntry(DateUtil.getTime(2017, 03, 24, 8, 0),
					DateUtil.getTime(2017, 03, 24, 18, 0));
			lb.setEmployee(empl1);
			lb.setPhase(new Phase(PhaseDescriptor.IMPLEMENTATION));
			dal.saveLogbookEntry(lb);

			dal.assignEmployeToIssue(empl1, i);

			i.setEffort(5);
			i.setEstimatedTime(20);

			dal.assignIssueToLogbookEntry(i, lb);

		} catch (ParseException e1) {
		}
	}

}
