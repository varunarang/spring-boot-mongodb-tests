package in.bushansirgur.restapi.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;
import in.bushansirgur.restapi.customer.Customer;
import in.bushansirgur.restapi.dto.GraphDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import in.bushansirgur.restapi.model.DatabaseSequence;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;


@Service
public class SequenceGeneratorService {

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired 
	private MongoOperations mongo;
	
	public long generateSequence(String seqName) {
	    DatabaseSequence counter = mongo.findAndModify(query(where("_id").is(seqName)),
	      new Update().inc("seq",1), options().returnNew(true).upsert(true),
	      DatabaseSequence.class);
	    return !Objects.isNull(counter) ? counter.getSeq() : 1;
	}

	public void getAggregatedCustomersByDate() {
		String appId = "";

		MatchOperation appointment1 = Aggregation.match(Criteria.where("appId").is(appId));

		GroupOperation groupOperation = Aggregation.group("date").count().as("dataCount");

		ProjectionOperation projectionOperation = Aggregation.project("dataCount").and("date").previousOperation();
		MatchOperation matchOperation = Aggregation.match(new Criteria("dataCount").gt(0));

		SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.ASC, "dataCount", "date"));

		Aggregation aggregation = Aggregation.newAggregation(appointment1, groupOperation, projectionOperation, matchOperation, sortOperation);

		AggregationResults<GraphDto> result = mongoTemplate.aggregate(aggregation, "appointment", GraphDto.class);

	}


	public List<Customer> getCustomers()	{
		Query query = new Query();
		query.fields().include("firstName").include("addedDate").exclude("id");
		List<Customer> customers = mongoTemplate.find(query, Customer.class);
		return customers;
	}


	public void getCustomersCountByDateAdded() {

		Date in = new Date();
		LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());

		Date out = Date.from(ldt.atZone(ZoneId.systemDefault()).minusYears(3).toInstant());

		MatchOperation matchStage = Aggregation.match(new Criteria("addedDate").gte(out));
		ProjectionOperation projectStage = Aggregation.project("firstName", "lastName");

		Aggregation aggregation
				= Aggregation.newAggregation(matchStage, projectStage);


		List<Document> docs = Arrays.asList(new Document("$match",
						new Document("date",
								new Document("$gte", new Date())
						)
				),
				new Document("$group",
					new Document("_id",
						new Document("$dateToString",
							new Document("format", "%Y-%m-%d")
								.append("date", out)))
						.append("count",
								new Document("$sum", 1L)
						)
				)
		);


//		AggregationResults<OutType> output
//				= mongoTemplate.aggregate(aggregation, "foobar", OutType.class);
		System.out.println("-----chull------");
		AggregateIterable<Document> customers = mongoTemplate.getCollection("customers").aggregate(docs);
		System.out.println(customers.iterator().next());
//		Query query = new BasicQuery(docs);
	}

	final String QUERY = "{" +
			"  ts: { $gte:  ISODate(?0 }" +
			"} " +
			"{" +
			"  _id: {" +
			"    $dateToString: {" +
			"      format: \"%Y-%m-%dT%H:%m\", date: \"$ts\"" +
			"    }" +
			"  }," +
			"  count: {" +
			"    $sum: 1" +
			"  }" +
			"}";
//	@Query(QUERY)
	public List<GraphDto> getCountGroupByDate(Date date) {

		List<Document> documents = Arrays.asList(new Document("$match",
						new Document("addedDate",
								new Document("$gte", date))
				),
				new Document("$group",
						new Document("_id",
								new Document("$dateToString",
										new Document("format", "%Y-%m-%dT%H:%m")
												.append("date", "$addedDate")))
								.append("count",
										new Document("$sum", 1L))));

		AggregateIterable<Document> result =
				mongoTemplate.getCollection("customers").aggregate(documents);

		for (Document res : result) {
			System.out.println(res.get("_id") + " --- " + res.get("count"));
		}

		return null;
	}



}
