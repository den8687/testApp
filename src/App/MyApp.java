package App;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

import asg.cliche.Command;
import asg.cliche.ShellFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import App.User;

public class MyApp {
	
	private static final String version = "0.1.20";
	
	private static final EntityManagerFactory entityManagerFactory
		= Persistence.createEntityManagerFactory("App.User");
	
	public static void main(String[] args) throws Exception {
		
		MyApp application = new MyApp();
		
		application.println(
			"Hello. This is the \"USER CRUD\" program.",
			"Type \"help\" to list available commands.");
		
		ShellFactory.createConsoleShell("USER", "", application).commandLoop();
		
	}
	
	@Command
	public void help() {
		
		println(
			"help           - print this message;",
			"version        - print current version of the program;",
			"create [name]  - create new user with specified name;",
			"delete [name]  - delete all users that matches specified name;",
			"list           - show a list of available users;",
			"find [pattern] - find and print users id and name that matches the pattern;",
			"exit           - exit the program;"
		);
	}
	
	@Command
	public void version() {
		
		println(
			"USER CRUD program (ver. "+version+")",
			"all rights reserved"
		);
	}
	
	@Command
	public void create(String... names) {
		
		List<User> users = new ArrayList<>();
		for (String userName : names)
			users.add(new User(userName));
		
		createUsers(users);
		println("done");
		
	}
	
	@Command
	public void delete(String name) {
	
		List<User> result = getUsersByName(name);
		if (result.isEmpty())
		{
			println("user \"" + name + "\" not found");
		}
		else
		{
			deleteUsers(result);
			println("user \"" + name + "\" deleted");
		}
	
	}
	
	@Command
	public void list() {
		
		List<User> result = getUsersByPattern("");
		
		if (result.isEmpty())
		{	
			println("there is no users yet");
			return;
		}
		
		println(result);
		
	}
	
	@Command
	public void find(String pattern) {
		
		List<User> users = getUsersByPattern(pattern);
		
		if (users.isEmpty())
		{
			println("no such users...");
			return;
		}
		
		println(users);
		
	}
	
	@Command
	public void exit() {
		
		println("see you next time...");
		System.exit(0);
	}
	
	/* Undocumented. For tests. */
	@Command
	public void fill() throws Exception {
		
		Scanner scanner = new Scanner(
			new File(".\\trg\\resources\\FakeNamesDataSet.txt"));
		
		List<User> users = new ArrayList<>();
		while (scanner.hasNextLine())
		{
			User aUser = new User(scanner.nextLine());
			users.add(aUser);
		}
		
		createUsers(users);
		println("database filled with fake users");
		
	}
	
	/* Undocumented. For tests. */
	@Command
	public void clear() {
		
		List<User> users = getUsersByPattern("");
		deleteUsers(users);
		println("database cleared");
		
	}

	private void println(String... messages) {
		
		System.out.println();
		
		for (String msg : messages)
			System.out.println("  " + msg);
			
		System.out.println();
		
	}

	private void println(List<User> users) {
		
		List<String> names = new ArrayList<>();
		for (User user : users)
			names.add(user.toString());
		
		String[] msg = {};
		msg = names.toArray(msg);
		
		println(msg);
		
	}

	private List<User> getUsersByPattern(String pattern) {
		
		EntityManager entityManager
			= MyApp.entityManagerFactory.createEntityManager();
		
		TypedQuery<User> query = entityManager.createQuery(
			"from User where name like :pattern", User.class)
			.setParameter("pattern", "%"+pattern+"%");
		
		entityManager.getTransaction().begin();
		
		List<User> users = query.getResultList();
		
		entityManager.getTransaction().commit();
		entityManager.close();
		
		return users;
		
	}

	private List<User> getUsersByName(String name) {
		
		EntityManager entityManager
			= MyApp.entityManagerFactory.createEntityManager();
		
		TypedQuery<User> query = entityManager.createQuery(
			"from User where name = :name", User.class)
			.setParameter("name", name);
		
		entityManager.getTransaction().begin();
		
		List<User> users = query.getResultList();
		
		entityManager.getTransaction().commit();
		entityManager.close();
		
		return users;
		
	} 

	private void createUsers(List<User> users) {
		
		EntityManager entityManager
			= MyApp.entityManagerFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		
		for (User usr : users) entityManager.persist(usr);
		
		entityManager.getTransaction().commit();
		entityManager.close();
		
	}

	private void deleteUsers(List<User> users) {
		
		EntityManager entityManager
			= MyApp.entityManagerFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		
		for (User usr : users)
			entityManager.remove(
				entityManager.contains(usr) ? usr : entityManager.merge(usr)
			);
		
		entityManager.getTransaction().commit();
		entityManager.close();
		
	}

}