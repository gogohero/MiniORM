package entities;

import java.sql.Date;

import annotations.*;


@Entity(name = "users")
public class User {
	
	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "username")
	private String username;
	@Column(name = "age")
	private int age;
	@Column(name = "registration_date")
	private Date registrationDate;
	
	
	
	public User(String username, int age, Date registrationDate) {
		super();
		this.username = username;
		this.age = age;
		this.registrationDate = registrationDate;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public Date getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	
	
	
}
 