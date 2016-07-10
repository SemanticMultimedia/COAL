package org.s16a.mcas;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.validator.routines.UrlValidator;

import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 * Root resource (exposed at "resource" path)
 */
@Path("resource")
public class ResourceHandler {

	/**
	 * use "text/turtle" for TURTLE
	 * deprecated: "application/x-turtle"
	 **/
	@GET
	@Produces({ "text/turtle", "application/x-turtle" })
	public String getTurtle(@QueryParam("url") String resourceUrl, @HeaderParam("accept") String acceptParam) throws MCASException, IOException, TimeoutException {
		Model model = getModel(resourceUrl, acceptParam);
		StringWriter out = new StringWriter();
		model.write(out, "TURTLE");
		return out.toString();
	}

	/**
	 * use "application/n-triples" for N-TRIPLES
	 * deprecated: "application/x-n3" and "application/x-ntriples"
	 **/
	@GET
	@Produces({ "application/n-triples", "application/x-n3", "application/x-ntriples" })
	public String getNTriples(@QueryParam("url") String resourceUrl, @HeaderParam("accept") String acceptParam) throws MCASException, IOException, TimeoutException {
		Model model = getModel(resourceUrl, acceptParam);
		StringWriter out = new StringWriter();
		model.write(out, "NTRIPLES");
		return out.toString();
	}

	/**
	 * use "application/ld+json" for JSON-LD
	 **/
	@GET
	@Produces({ "application/ld+json" })
	public String getJsonLD(@QueryParam("url") String resourceUrl, @HeaderParam("accept") String acceptParam) throws MCASException, IOException, TimeoutException {
		Model model = getModel(resourceUrl, acceptParam);
		StringWriter out = new StringWriter();
		model.write(out, "JSONLD");
		return out.toString();
	}

	/**
	 * default ?
	 **/
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt(@QueryParam("url") String resourceUrl, @HeaderParam("accept") String acceptParam) throws MCASException, IOException, TimeoutException {
		Model model = getModel(resourceUrl, acceptParam);
		StringWriter out = new StringWriter();
		model.write(out, "TURTLE");
		return out.toString();
	}

	private Model getModel(String resourceUrl, String acceptParam) throws MCASException, IOException, TimeoutException {
		// (1) check url validity
		// (2) check return format
		// (3) create hash
		// (4) check file resource - if existing load model and return it
		// (5) check url header (size and format)
		// (6) create and store basic model
		// (7) enqueue for download
		// (7b) enqueue resource
		// (8) return model --> send accept

		// (1)
		UrlValidator urlValidator = new UrlValidator();
		if (!urlValidator.isValid(resourceUrl)) {
			throw new MCASException("URL is not valid: " + resourceUrl);
		}

		// (2)
		System.out.println("accept: " + acceptParam);
		if (!checkAcceptParam(acceptParam)) {
			throw new MCASException("invalid accept header");
		}

		// (3)
		Cache cache = new Cache(resourceUrl);
		String filename = cache.getFilePath("data.ttl");

		// (4)
		File f = new File(filename);
		if (f.exists()) {
			Model model = ModelFactory.createDefaultModel();
			model.read(filename);
			return model;
		}

		// (5)
		URL url = new URL(resourceUrl);
		URLConnection conn = url.openConnection();

		// get URLs headers
		Map<String, List<String>> map = conn.getHeaderFields();
		int MAX_CONTENT_LENGTH = 500000000; // ca. 500MB
		Set<String> VALID_CONTENT_TYPES = new HashSet<String>(Arrays.asList("image/jpeg", "image/png", "audio/x-mpeg-3", "audio/mpeg3", "audio/x-mpeg", "audio/mpeg", "audio/mp3", "audio/wav", "audio/x-wav", "audio/vnd.wave"));

		int contentLength = Integer.parseInt(map.get("Content-Length").get(0));
		String contentType = map.get("Content-Type").get(0);

		if (contentLength > MAX_CONTENT_LENGTH) {
			System.out.println("file too huge");
			throw new MCASException("content length exceeded");
		}
		if (!VALID_CONTENT_TYPES.contains(contentType)) {
			System.out.println("invalid  content type");
			throw new MCASException("content type invalid");
		}

		// (6)
		Model model = createAndStoreBasicModel(url, filename, map);

		// (7)
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(System.getenv().get("RABBIT_HOST"));
        factory.setUsername("coal");
        factory.setPassword("coal");

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.queueDeclare(MCAS.download.toString(), true, false, false, null);

        System.out.println(" [>] Channel '" + channel + "'");

        String message = resourceUrl;
		channel.basicPublish("", MCAS.download.toString(), MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());

		System.out.println(" [x] Sent '" + message + "'");

		channel.close();
		connection.close();
		
		return model;
	}

	private Model createAndStoreBasicModel(URL url, String filename, Map<String, List<String>> map) throws IOException {

		String COAL_SERVER_URI = "http://coal.s16a.org/resource";
		String MEDIA_URI = url.toString();
		Model model = ModelFactory.createDefaultModel();

		Map<String, String> nsPrefixes = new HashMap<>();
		String foaf = "http://xmlns.com/foaf/0.1/";
		nsPrefixes.put("foaf", foaf);
		String dc = "http://purl.org/dc/elements/1.1/";
		nsPrefixes.put("dc", dc);
		String dcterms = "http://purl.org/dc/terms/";
		nsPrefixes.put("dcterms", dcterms);
        String xsd = "http://www.w3.org/2001/XMLSchema#";
        nsPrefixes.put("xsd", xsd);
        model.setNsPrefixes(nsPrefixes);


		Resource rdfDocument = model.createResource(COAL_SERVER_URI + "?url=" + MEDIA_URI);
		rdfDocument.addProperty(RDF.type, foaf + "Document");
        Property topic = model.createProperty(foaf + "topic");
		rdfDocument.addLiteral(topic, MEDIA_URI);
		rdfDocument.addProperty(model.createProperty(foaf + "maker"), "COAL");
		rdfDocument.addProperty(DC.identifier, filename);

        Resource file = model.createResource(MEDIA_URI);
        int contentLength = Integer.parseInt(map.get("Content-Length").get(0));
        String contentType = map.get("Content-Type").get(0);
        String lastModified = map.get("Last-Modified").get(0);
        file.addLiteral(DC.format, contentType);
        file.addLiteral(DCTerms.extent, contentLength);
        file.addLiteral(DCTerms.modified, lastModified);

		FileWriter out = new FileWriter(filename);

		try {
			model.write(out, "TURTLE");
		} finally {
			try {
				out.close();
			} catch (IOException closeException) {
				System.out.println(closeException.getMessage());
			}
		}

		return model;
	}

	private boolean checkAcceptParam(String acceptParam) {

		return true;
	}
}