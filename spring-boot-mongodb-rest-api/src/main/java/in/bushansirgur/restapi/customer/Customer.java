package in.bushansirgur.restapi.customer;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection="customers")
public class Customer {

    @Id
    public String id;

    public String firstName;

    @Indexed
    public String lastName;


    public Date addedDate;

    public Customer() {}

    public Customer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
//        this.addedDate = new Date();
    }

    public Date getAddedDate() {
        return this.addedDate;
    }

    public void setAddedDate(Date d) {
        this.addedDate = d;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%s, firstName='%s', lastName='%s', added='%s']",
                id, firstName, lastName, addedDate);
    }

}

