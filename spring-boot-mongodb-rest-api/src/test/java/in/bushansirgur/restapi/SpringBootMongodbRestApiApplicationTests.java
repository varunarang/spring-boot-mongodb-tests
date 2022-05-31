package in.bushansirgur.restapi;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import in.bushansirgur.restapi.dto.GraphDto;
import in.bushansirgur.restapi.service.SequenceGeneratorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.data.domain.Example;

import in.bushansirgur.restapi.customer.Customer;
import in.bushansirgur.restapi.customer.CustomerRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = "spring.mongodb.embedded.version=5.0.6")
public class SpringBootMongodbRestApiApplicationTests {

    SimpleDateFormat formatter5 = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
    @Autowired
    CustomerRepository repository;

    @Autowired
    SequenceGeneratorService service;

    Customer dave, oliver, oliver2, carter, carter2, carter3;

    Date out;

    @Test
    public void contextLoads() {
    }

    @Before
    public void setUp() throws ParseException {

        repository.deleteAll();

        //dave
        dave = new Customer("Dave", "Matthews");
        String daveRegisterDate = "Dec 31 2021 23:37:50";
        dave.setAddedDate(formatter5.parse(daveRegisterDate));
        repository.save(dave);

        // Oliver
        oliver = new Customer("Oliver August", "Matthews");
        String oliverRegisterDate = "Dec 13 2021 13:37:50";
        Date oliverDate = formatter5.parse(oliverRegisterDate);
        oliver.setAddedDate(oliverDate);
        oliver = repository.save(oliver);

        oliver2 = new Customer("Oliver August2", "Matthews");
        String oliver2RegisterDate = "Dec 13 2021 13:47:50";
        oliver2.setAddedDate(formatter5.parse(oliver2RegisterDate));
        repository.save(oliver2);

        // Carter
        carter = new Customer("Carter", "Beauford");
        String carterRegisterDate = "Dec 31 2021 11:12:13";
        carter.setAddedDate(formatter5.parse(carterRegisterDate));
        repository.save(carter);

        carter2 = new Customer("Carter2", "Beauford2");
        String carter2RegisterDate = "Dec 31 2021 12:12:13";
        carter2.setAddedDate(formatter5.parse(carter2RegisterDate));
        repository.save(carter2);

        carter3 = new Customer("Carter3", "Beauford3");
        String carter3RegisterDate = "Dec 31 2021 13:12:13";
        carter3.setAddedDate(formatter5.parse(carter3RegisterDate));
        repository.save(carter3);

        LocalDateTime ldt = LocalDateTime.ofInstant(oliverDate.toInstant(), ZoneId.systemDefault());

        out = Date.from(ldt.atZone(ZoneId.systemDefault()).minusYears(3).toInstant());

    }

    @Test
    public void setsIdOnSave() {

        Customer dave = repository.save(new Customer("Dave", "Matthews"));

        assertThat(dave.id).isNotNull();
    }

    @Test
    public void findsByLastName() {

        List<Customer> result = repository.findByLastName("Beauford");

        assertThat(result).hasSize(1).extracting("firstName").contains("Carter");
    }



    @Test
    public void findsByExample() {

        Customer probe = new Customer(null, "Matthews");

        List<Customer> result = repository.findAll(Example.of(probe));

        assertThat(result).hasSize(3).extracting("firstName").contains("Dave", "Oliver August");
    }

    @Test
    public void getCustomers() {
        List<Customer> customers = service.getCustomers();
        assertNotEquals(customers.size(), 0);
    }


    @Test
    public void getCustomerLastNameTest(){
        List<Customer> customers = repository.findByLastNameAndExcludeId();
        assertNull(customers.get(0).firstName);
        assertNull(customers.get(customers.size() - 1).firstName);
//        service.getCustomersCountByDateAdded();
    }


    @Test
    public void getGroupedDataTest() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
        String nowAsISO = df.format(out);
//        List<GraphDto> list =  repository.getCountGroupByDate(nowAsISO);
//        assertNotNull(list);

        service.getCountGroupByDate(out);
    }

}

