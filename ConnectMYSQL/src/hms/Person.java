package hms;

import java.sql.Date;


public abstract class Person {
  private int personID;
  private String name;
  private String phoneNumber;
  private String gender;
  private Date dateOfBirth;
  private String email;

  public int getPersonID() {
    return personID;
  }

  public String getName() {
    return name;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }


  public String getGender() {
    return gender;
  }

  public Date getDateOfBirth() {
    return dateOfBirth;
  }

  public void setPersonID(int personID) {
    this.personID = personID;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmail() {
    return email;
}

// Setter for email
public void setEmail(String email) {
    this.email = email;
}

  public void setGender(String gender) {
    this.gender = gender;
  }

  public void setDateOfBirth(Date date) {
    this.dateOfBirth = date;
  }
public Person(int personID, String name, String phoneNumber, String gender, Date dateOfBirth, String email) {
        this.personID = personID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
    }
public Person( String name, String phoneNumber, String gender, Date dateOfBirth, String email) {
        
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
    }
public Person(){}
}
