package de.wwu.music2rdf.converter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import de.wwu.music2rdf.core.Collection;
import de.wwu.music2rdf.core.Person;
import de.wwu.music2rdf.core.ScoreResource;
import de.wwu.music2rdf.util.Util;

public class Converter {

	private static Logger logger = Logger.getLogger("Parser");
	
	public static void main(String[] args) {

		MusicXML2RDF music2rdf = new MusicXML2RDF();

		System.out.println("\n");
		for(int i = 0; i < args.length; i++) {

			if(args[i].toLowerCase().equals("version")){

				System.out.println("Music2RDF Converter ");
				System.out.println("Version: "+music2rdf.getVersion());
				System.exit(0);

			}

			String[] parameter = args[i].split("=");

			if(parameter[0].toLowerCase().equals("file")){
				System.out.println("File  	        : " + parameter[1]);
				music2rdf.setInputFile(new File(parameter[1]));
			}

			if(parameter[0].toLowerCase().equals("output")){
				music2rdf.setOutputFile(parameter[1]);
				System.out.println("Output File     : " + parameter[1]);
			}

			if(parameter[0].toLowerCase().equals("verbose")){
				System.out.println("Verbose      	: " + parameter[1]);				
				music2rdf.isVerbose(Boolean.valueOf(parameter[1]));				
			}

			if(parameter[0].toLowerCase().equals("outputformat")){
				System.out.println("OutputFormat    : " + parameter[1]);				
				music2rdf.setOutputFormat(parameter[1]);				
			}

			if(parameter[0].toLowerCase().equals("metadata")){

				File metadataFile = new File(parameter[1]);

				if(Util.isXMLValid(metadataFile)) {

					try {

						DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
						DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
						Document doc = dBuilder.parse(metadataFile);
						doc.getDocumentElement().normalize();

						NodeList nList = doc.getElementsByTagName("score");

						for (int j = 0; j < nList.getLength(); j++) {

							Node nNode = nList.item(j);

							if (nNode.getNodeType() == Node.ELEMENT_NODE) {

								Element scoreElement = (Element) nNode;

								System.out.println("Score URI       : " + scoreElement.getElementsByTagName("scoreIdentifier").item(0).getTextContent());
								System.out.println("Title           : " + scoreElement.getElementsByTagName("title").item(0).getTextContent());
								System.out.println("Date Issued     : " + scoreElement.getElementsByTagName("issued").item(0).getTextContent());
								System.out.println("Thumbnail       : " + scoreElement.getElementsByTagName("thumbnail").item(0).getTextContent());

								music2rdf.setScoreURI(scoreElement.getElementsByTagName("scoreIdentifier").item(0).getTextContent());
								music2rdf.setDocumentTitle(scoreElement.getElementsByTagName("title").item(0).getTextContent());
								music2rdf.setDateIssued(scoreElement.getElementsByTagName("issued").item(0).getTextContent());
								music2rdf.setThumbnail(scoreElement.getElementsByTagName("thumbnail").item(0).getTextContent());

								Element personsElement = (Element) scoreElement.getElementsByTagName("persons").item(0);

								NodeList personList = personsElement.getElementsByTagName("person");

								for (int k = 0; k < personList.getLength(); k++) {

									Node personNode = personList.item(k);

									if (personNode.getNodeType() == Node.ELEMENT_NODE) {

										Element personElement = (Element) personNode;

										System.out.println("Person URI      : " + personElement.getElementsByTagName("personIdentifier").item(0).getTextContent());
										System.out.println("Person Name     : " + personElement.getElementsByTagName("personName").item(0).getTextContent());
										System.out.println("Person Role     : " + personElement.getElementsByTagName("personRole").item(0).getTextContent());

										String personURI = personElement.getElementsByTagName("personIdentifier").item(0).getTextContent();
										String personName = personElement.getElementsByTagName("personName").item(0).getTextContent();
										String personRole = personElement.getElementsByTagName("personRole").item(0).getTextContent();

										music2rdf.addPerson(new Person(personURI,personName,personRole));

									}
								}


								Element resourcesElement = (Element) scoreElement.getElementsByTagName("resources").item(0);

								NodeList resourceList = resourcesElement.getElementsByTagName("resource");

								for (int k = 0; k < resourceList.getLength(); k++) {

									Node resourceNode = resourceList.item(k);

									if (resourceNode.getNodeType() == Node.ELEMENT_NODE) {

										Element personElement = (Element) resourceNode;

										System.out.println("Resource URI    : " + personElement.getElementsByTagName("resourceURL").item(0).getTextContent());
										System.out.println("Resource Desc.  : " + personElement.getElementsByTagName("resourceDescription").item(0).getTextContent());
										System.out.println("Resource Type   : " + personElement.getElementsByTagName("resourceType").item(0).getTextContent());

										String resourceURI = personElement.getElementsByTagName("resourceURL").item(0).getTextContent();
										String resourceDescription = personElement.getElementsByTagName("resourceDescription").item(0).getTextContent();
										String resourceType = personElement.getElementsByTagName("resourceType").item(0).getTextContent();

										music2rdf.addResource(new ScoreResource(resourceURI, resourceDescription,resourceType));
									}
								}


								Element collectionsElement = (Element) scoreElement.getElementsByTagName("collections").item(0);

								NodeList collectionList = collectionsElement.getElementsByTagName("collection");

								for (int k = 0; k < collectionList.getLength(); k++) {

									Node collectionNode = collectionList.item(k);

									if (collectionNode.getNodeType() == Node.ELEMENT_NODE) {

										Element collectionElement = (Element) collectionNode;

										System.out.println("Collection URI  : " + collectionElement.getElementsByTagName("collectionURL").item(0).getTextContent());
										System.out.println("Collection Name : " + collectionElement.getElementsByTagName("collectionName").item(0).getTextContent());


										String collectionURL = collectionElement.getElementsByTagName("collectionURL").item(0).getTextContent();
										String collectionName = collectionElement.getElementsByTagName("collectionName").item(0).getTextContent();

										music2rdf.addCollection(new Collection(collectionURL,collectionName));
									}
								}

							}

						}


					} catch (ParserConfigurationException e) {
						e.printStackTrace();
					} catch (SAXException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else if (Util.isJSONValid(metadataFile)) {
					
					logger.info("Parsing JSON metadata file: " + metadataFile.getAbsolutePath() );
					
					try {
						
						JSONParser parser = new JSONParser();	
						String str = FileUtils.readFileToString(metadataFile, StandardCharsets.UTF_8);						
						Object obj = parser.parse(str);
						JSONObject jsonObject = (JSONObject) obj;
												
						System.out.println("\nScore URI       : " + jsonObject.get("scoreIdentifier").toString());
						System.out.println("Title           : " + jsonObject.get("title").toString());
						System.out.println("Date Issued     : " + jsonObject.get("issued").toString());
						System.out.println("Thumbnail       : " + jsonObject.get("thumbnail").toString());

						music2rdf.setScoreURI(jsonObject.get("scoreIdentifier").toString());
						music2rdf.setDocumentTitle(jsonObject.get("title").toString());
						music2rdf.setDateIssued(jsonObject.get("issued").toString());
						music2rdf.setThumbnail(jsonObject.get("thumbnail").toString());

						JSONArray collections = (JSONArray) jsonObject.get("collections");
						
						for (int j = 0; j < collections.size(); j++) {
							
							JSONObject collection = (JSONObject) collections.get(j);

							music2rdf.addCollection(new Collection(collection.get("collectionURL").toString(),collection.get("collectionName").toString()));
							
							System.out.println("Collection URI  : " + collection.get("collectionURL").toString());
							System.out.println("Collection Name : " + collection.get("collectionName").toString());
							
						}
						
						JSONArray persons = (JSONArray) jsonObject.get("persons");
						
						for (int j = 0; j < persons.size(); j++) {
							
							JSONObject person = (JSONObject) persons.get(j);
							
							System.out.println("Person URI      : " + person.get("personIdentifier").toString());
							System.out.println("Person Name     : " + person.get("personName").toString());
							System.out.println("Person Role     : " + person.get("personRole").toString());

							music2rdf.addPerson(new Person(person.get("personIdentifier").toString(),
														   person.get("personName").toString(),
														   person.get("personRole").toString()));
							
						}
												
						JSONArray resources = (JSONArray) jsonObject.get("resources");
						
						for (int j = 0; j < resources.size(); j++) {
							
							JSONObject resource = (JSONObject) resources.get(j);
							
							System.out.println("Resource URI    : " + resource.get("resourceURL").toString());
							System.out.println("Resource Desc.  : " + resource.get("resourceDescription").toString());
							System.out.println("Resource Type   : " + resource.get("resourceType").toString());

							music2rdf.addResource(new ScoreResource(resource.get("resourceURL").toString(), 
																	resource.get("resourceDescription").toString(), 
																	resource.get("resourceType").toString()));
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					
					
				} else {
					
					logger.fatal("Invalid fetadata file: " + metadataFile.getAbsolutePath());
				}
			}

		}

		System.out.println("\n");

		music2rdf.parseMusicXML();

	}


}