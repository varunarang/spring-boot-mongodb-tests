package in.bushansirgur.restapi.service;

import in.bushansirgur.restapi.customer.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SequenceGeneratorServiceTest {

    @Autowired
    SequenceGeneratorService service;

    @Test
    public void getAggregatedCustomersByDate() {
    }


}