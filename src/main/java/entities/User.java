package entities;

import orm.annotations.Column;
import orm.annotations.Entity;
import orm.annotations.PrimaryKey;

import java.util.Date;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(columnName = "id")
    private long id;

    @Column(name="username")
    private String userName;

    @Column(name="age")
    private int age;

    @Column(name="registration_date")
    private Date registrationDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    @Override
    public String toString() {
        return String.format("| %d | %s | %d | %s", getId(),getUserName(),getAge(),getRegistrationDate() == null ? "" : getRegistrationDate().toString() );
    }
}
