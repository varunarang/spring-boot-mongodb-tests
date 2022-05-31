package in.bushansirgur.restapi.customer;

import java.util.Date;
import java.util.List;

import in.bushansirgur.restapi.dto.GraphDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "customers", path = "customers")
public interface CustomerRepository extends MongoRepository<Customer, String> {

    final String QUERY2 = "{" +
            "  _id: {" +
            "    $dateToString: {" +
            "      format: \"%Y-%m-%dT%H:%m\", date: \"$addedDate\"" +
            "    }" +
            "  }," +
            "  count: {" +
            "    $sum: 1" +
            "  }" +
            "}";
    final String QUERY = "{" +
            "  addedDate: { $gte:  '?0' }" +
            "}, " +
            "{" +
            "  _id: {" +
            "    $dateToString: {" +
            "      format: \"%Y-%m-%dT%H:%m\", date: \"$addedDate\"" +
            "    }" +
            "  }," +
            "  count: {" +
            "    $sum: 1" +
            "  }" +
            "}";
    List<Customer> findByLastName(@Param("name") String lastName);

    @Query(value="{}", fields="{lastName : 1, _id : 0}")
    List<Customer> findByLastNameAndExcludeId();


    @Query(value = QUERY)
    public List<GraphDto> getCountGroupByDate(String date);
}
