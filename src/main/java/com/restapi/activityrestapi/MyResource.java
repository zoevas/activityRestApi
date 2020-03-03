package com.restapi.activityrestapi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;

/**
 * Root resource (exposed at "activities" path)
 */
@Path("activities")
public class MyResource {

	private RepositoryConnection connection;

	private static final String NAMESPACE = "PREFIX a: <http://www.semanticweb.org/user/ontologies/2020/1/activity#> \n"; 
    
	public void initializeRepo() {
		// Abstract representation of a remote repository accessible over HTTP
		HTTPRepository repository = new HTTPRepository("http://localhost:7200/repositories/activity");

		// Separate connection to a repository
		connection = repository.getConnection();
	}
	
	public String listAllActivities(RepositoryConnection connection) {
		System.out.println("\n--------------------------------\n ");
		System.out.println("# Listing all activities ");
		System.out.println("\n--------------------------------\n ");
		String activities = "";
		
		String queryString = NAMESPACE;
		
		queryString += "SELECT ?a ?sd ?ed ?c\n";
		queryString += "WHERE { \n";
		queryString += "    ?a a:hasElement  ?e . \n";
		queryString += "    ?a a a:Activity . \n";
		queryString += "    ?e a:hasStartDate ?sd . \n";
		queryString += "    ?e a:hasEndDate ?ed . \n";
		queryString += "    ?e a:hasContentString ?c .";
		queryString += "}";
		
		TupleQuery query = connection.prepareTupleQuery(queryString);
		
		TupleQueryResult result = query.evaluate();
		while (result.hasNext()) {
			BindingSet bindingSet = result.next();
			
			IRI a = (IRI) bindingSet.getBinding("a").getValue();
			
			activities += a.getLocalName() + ", start date =" + bindingSet.getValue("sd").stringValue() + ", end date =" + bindingSet.getValue("ed").stringValue() + ", content string =" + bindingSet.getValue("c").stringValue() + "\n";
			System.out.println(a.getLocalName() + "\n\t has start date " + bindingSet.getValue("sd") + "\n\t has end date " + bindingSet.getValue("ed") + "\n\t has content string  " + bindingSet.getValue("c"));
				
		}
		result.close();
		return activities;
	}
	
	/**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getActivities() {
    	initializeRepo();
    	String activities = "";
    	try {
			activities = listAllActivities(connection);
		} finally {
			connection.close();
		}
        return activities;
    }
}
