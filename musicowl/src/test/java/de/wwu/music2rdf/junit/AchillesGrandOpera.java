package de.wwu.music2rdf.junit;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.log4j.Logger;
import org.junit.Test;

public class AchillesGrandOpera {

	private static Logger logger = Logger.getLogger("AchillesGrandOpera-Test");
	
	@Test
	public void melodyWithDots() {

		String result = "";				
		URL url = this.getClass().getResource("/rdf/achilles_grand-opera.ttl");
		File file = new File(url.getFile());

		Model model = ModelFactory.createDefaultModel();
		model.read(file.getAbsolutePath(),"N-TRIPLES");
		
		String sparql = "PREFIX mso: <http://linkeddata.uni-muenster.de/ontology/musicscore#>\n" + 
				"PREFIX chord: <http://purl.org/ontology/chord/>\n" + 
				"PREFIX note: <http://purl.org/ontology/chord/note/>\n" + 
				"PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"PREFIX mo: <http://purl.org/ontology/mo/>\n" + 
				"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" + 
				"PREFIX dbo: <http://dbpedia.org/property/> \n"+
				"SELECT DISTINCT ?scoreNode ?scoreTitle ?movement ?partName ?voice ?measure\n" + 
				"WHERE {\n" + 
				"\n" + 
				"	<https://sammlungen.ulb.uni-muenster.de/id/5731633> dc:title ?scoreTitle.\n" + 
				"	<https://sammlungen.ulb.uni-muenster.de/id/5731633> mo:movement ?movementNode.\n" + 
				"	?movementNode dc:title ?movemenTitle.\n" + 
				"	<https://sammlungen.ulb.uni-muenster.de/id/5731633> dc:creator ?creatorNode.\n" + 
				"	?creatorNode foaf:name ?creator.\n" + 
				"	?movementNode mso:hasScorePart ?part.\n" + 
				"	?movementNode dc:title ?movement.\n" + 
				"	?part mso:hasMeasure ?measureNode.\n" + 
				"	?part rdfs:label ?partID.\n" + 
				"	?part dc:description ?partName.\n" + 
				"	?part mso:hasStaff ?staff.\n" + 
				"	?measureNode dbo:order ?measure.\n" + 
				"	?voice a mso:Voice.\n" + 
				"	?voice rdfs:label ?voiceID.\n" + 
				"	?measureNode mso:hasNoteSet ?noteset0.\n" + 
				"	?staff mso:hasVoice ?voice.\n" + 
				"	?staff rdfs:label ?staffID.\n" + 
				"\n" + 
				"	?noteset0 mso:hasNote ?note0.   \n" + 
				"	?voice mso:hasNoteSet ?noteset0.\n" + 
				"	?note0 chord:natural note:D.\n" + 
				"    ?note0 mso:hasOctave \"5\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" + 
				"	?noteset0 mso:hasDuration ?duration0.\n" + 
				"	?duration0 a mso:16th.\n" + 
				"	?noteset0 mso:nextNoteSet ?noteset1.\n" + 
				"\n" + 
				"	?noteset1 mso:hasNote ?note1.\n" + 
				"	?voice mso:hasNoteSet ?noteset1.\n" + 
				"	?note1 chord:natural note:F.\n" + 
				"	?note1 mso:hasOctave \"5\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" + 
				"	?noteset1 mso:hasDuration ?duration1.\n" + 
				"	?duration1 a mso:16th.\n" + 
				"	?noteset1 mso:nextNoteSet ?noteset2.\n" + 
				"\n" + 
				"	?noteset2 mso:hasNote ?note2.\n" + 
				"	?voice mso:hasNoteSet ?noteset2.\n" + 
				"	?note2 chord:natural note:E.\n" + 
				"    ?note5 mso:hasOctave \"5\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" + 
				"	?note2 chord:modifier chord:flat.    \n" + 
				"	?noteset2 mso:hasDuration ?duration2.\n" + 
				"	?duration2 a mso:16th.\n" + 
				"	?noteset2 mso:nextNoteSet ?noteset3.\n" + 
				"\n" + 
				"	?noteset3 mso:hasNote ?note3.\n" + 
				"    ?note3 mso:hasOctave \"5\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" + 
				"	?voice mso:hasNoteSet ?noteset3.\n" + 
				"	?note3 chord:natural note:D.\n" + 
				"	?noteset3 mso:hasDuration ?duration3.\n" + 
				"	?duration3 a mso:16th.\n" + 
				"	?noteset3 mso:nextNoteSet ?noteset4.\n" + 
				"\n" + 
				"	?noteset4 mso:hasNote ?note4.\n" + 
				"	?voice mso:hasNoteSet ?noteset4.\n" + 
				"	?note4 chord:natural note:E.\n" + 
				"    ?note4 mso:hasOctave \"5\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" + 
				"	?note4 chord:modifier chord:flat.    \n" + 
				"	?noteset4 mso:hasDuration ?duration4.\n" + 
				"	?duration4 a mso:16th.\n" + 
				"	?noteset4 mso:nextNoteSet ?noteset5.\n" + 
				"    \n" + 
				"	?noteset5 mso:hasNote ?note5.\n" + 
				"	?voice mso:hasNoteSet ?noteset5.\n" + 
				"	?note5 chord:natural note:F.\n" + 
				"    ?note5 mso:hasOctave \"5\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" + 
				"	?noteset5 mso:hasDuration ?duration5.\n" + 
				"	?duration5 a mso:16th.\n" + 
				"	?noteset5 mso:nextNoteSet ?noteset6.\n" + 
				"    \n" + 
				"    ?noteset6 mso:hasNote ?note6.\n" + 
				"	?voice mso:hasNoteSet ?noteset6.\n" + 
				"	?note6 chord:natural note:A.\n" + 
				"    ?note6 chord:modifier chord:flat.    \n" + 
				"    ?note6 mso:hasOctave \"4\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" + 
				"	?noteset6 mso:hasDuration ?duration6.\n" + 
				"	?duration6 a mso:Quarter.\n" + 
				"	?noteset6 mso:nextNoteSet ?noteset7.\n" + 
				"    \n" + 
				"    ?noteset7 mso:hasNote ?note7.\n" + 
				"	?voice mso:hasNoteSet ?noteset7.\n" + 
				"	?note7 chord:natural note:C.\n" + 
				"    ?note7 mso:hasOctave \"5\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" + 
				"	?noteset7 mso:hasDuration ?duration7.\n" + 
				"	?duration7 a mso:Eighth.\n" + 
				"    ?duration7 mso:hasDurationAttribute ?attribute_note7 . \n" + 
				"	?attribute_note7 a mso:Dot.\n" + 
				"	?noteset7 mso:nextNoteSet ?noteset8.\n" + 
				"    \n" + 
				"    ?noteset8 mso:hasNote ?note8.\n" + 
				"	?voice mso:hasNoteSet ?noteset8.\n" + 
				"	?note8 chord:natural note:B.\n" + 
				"    ?note8 mso:hasOctave \"4\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" + 
				"    ?note8 chord:modifier chord:flat.    \n" + 
				"	?noteset8 mso:hasDuration ?duration8.\n" + 
				"	?duration8 a mso:16th.\n" + 
				"	?noteset8 mso:nextNoteSet ?noteset9.\n" + 
				"\n" + 
				"    ?noteset9 mso:hasNote ?note9.\n" + 
				"	?voice mso:hasNoteSet ?noteset9.\n" + 
				"	?note9 chord:natural note:B.\n" + 
				"    ?note9 mso:hasOctave \"4\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" + 
				"    ?note9 chord:modifier chord:flat.    \n" + 
				"	?noteset9 mso:hasDuration ?duration9.\n" + 
				"    ?duration9 mso:hasDurationAttribute ?attribute_note9. \n" + 
				"	?attribute_note9 a mso:Dot.\n" + 
				"	?duration9 a mso:Eighth.\n" + 
				"	    \n" + 
				"	FILTER ( NOT EXISTS {?note0 chord:modifier ?modifier0} )\n" + 
				"	FILTER ( NOT EXISTS {?note1 chord:modifier ?modifier1} )\n" + 
				"	FILTER ( NOT EXISTS {?note3 chord:modifier ?modifier3} )\n" + 
				"   FILTER ( NOT EXISTS {?note5 chord:modifier ?modifier5} )\n" + 
				"   FILTER ( NOT EXISTS {?note7 chord:modifier ?modifier7} )\n" + 
				"}";
		
		try (QueryExecution qexec = QueryExecutionFactory.create(sparql, model)) {
			ResultSet results = qexec.execSelect() ;
			for ( ; results.hasNext() ; )
			{
				QuerySolution soln = results.nextSolution() ;
				result = soln.get("?measure").toString();
				logger.info("Melody containing dots in multiple durations: Measure " + result);
				
			}
		}

		assertEquals(result, "6");			
	}
	
	
	@Test
	public void melodyWithRestAndDynamic() {

		String result = "";		
		URL url = this.getClass().getResource("/rdf/achilles_grand-opera.ttl");
		File file = new File(url.getFile());

		Model model = ModelFactory.createDefaultModel();
		model.read(file.getAbsolutePath(),"N-TRIPLES");
		String sparql = "PREFIX mso: <http://linkeddata.uni-muenster.de/ontology/musicscore#>\n" + 
				"PREFIX chord: <http://purl.org/ontology/chord/>\n" + 
				"PREFIX note: <http://purl.org/ontology/chord/note/>\n" + 
				"PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"PREFIX mo: <http://purl.org/ontology/mo/>\n" + 
				"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" + 
				"PREFIX dbo: <http://dbpedia.org/property/> \n"+
				"SELECT DISTINCT ?scoreNode ?scoreTitle ?movement ?partName ?voice ?measure \n" + 
				"WHERE {\n" + 
				"\n" + 
				"	<https://sammlungen.ulb.uni-muenster.de/id/5731633> dc:title ?scoreTitle.\n" + 
				"	<https://sammlungen.ulb.uni-muenster.de/id/5731633> mo:movement ?movementNode.\n" + 
				"	?movementNode dc:title ?movemenTitle.\n" + 
				"	<https://sammlungen.ulb.uni-muenster.de/id/5731633> dc:creator ?creatorNode.\n" + 
				"	?creatorNode foaf:name ?creator.\n" + 
				"	?movementNode mso:hasScorePart ?part.\n" + 
				"	?movementNode dc:title ?movement.\n" + 
				"	?part mso:hasMeasure ?measureNode.\n" + 
				"	?part rdfs:label ?partID.\n" + 
				"	?part dc:description ?partName.\n" + 
				"	?part mso:hasStaff ?staff.\n" + 
				"	?measureNode dbo:order ?measure.\n" + 
				"	?voice a mso:Voice.\n" + 
				"	?voice rdfs:label ?voiceID.\n" + 
				"	?measureNode mso:hasNoteSet ?noteset0.\n" + 
				"	?staff mso:hasVoice ?voice.\n" + 
				"	?staff rdfs:label ?staffID.\n" + 
				"\n" + 
				"	?noteset0 mso:hasNote ?note0.   \n" + 
				"    ?noteset0 mso:hasDynamic ?dynamic.\n" + 
				"    ?dynamic a mso:sf.\n" + 
				"	?voice mso:hasNoteSet ?noteset0.\n" + 
				"	?note0 chord:natural note:B.\n" + 
				"    ?note0 mso:hasOctave \"4\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" + 
				"	?noteset0 mso:hasDuration ?duration0.\n" + 
				"	?duration0 a mso:Eighth.\n" + 
				"    ?note0 chord:modifier chord:flat.    \n" + 
				"	?noteset0 mso:nextNoteSet ?noteset1.\n" + 
				"\n" + 
				"	?noteset1 mso:hasNote ?note1.\n" + 
				"	?voice mso:hasNoteSet ?noteset1.\n" + 
				"	?note1 chord:natural note:B.\n" + 
				"	?note1 mso:hasOctave \"4\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" + 
				"	?noteset1 mso:hasDuration ?duration1.\n" + 
				"	?duration1 a mso:32nd.\n" + 
				"    ?note1 chord:modifier chord:flat.    \n" + 
				"	?noteset1 mso:nextNoteSet ?noteset2.\n" + 
				"\n" + 
				"	?noteset2 mso:hasNote ?note2.\n" + 
				"	?voice mso:hasNoteSet ?noteset2.\n" + 
				"	?note2 chord:natural note:C.\n" + 
				"    ?note2 mso:hasOctave \"5\"^^<http://www.w3.org/2001/XMLSchema#int> .	\n" + 
				"	?noteset2 mso:hasDuration ?duration2.\n" + 
				"	?duration2 a mso:32nd.\n" + 
				"	?noteset2 mso:nextNoteSet ?noteset3.\n" + 
				"\n" + 
				"	?noteset3 mso:hasNote ?note3.\n" + 
				"    ?note3 mso:hasOctave \"4\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" + 
				"	?voice mso:hasNoteSet ?noteset3.\n" + 
				"	?note3 chord:natural note:B.\n" + 
				"   	?note3 chord:modifier chord:flat.    \n" + 
				"	?noteset3 mso:hasDuration ?duration3.\n" + 
				"	?duration3 a mso:32nd.\n" + 
				"	?noteset3 mso:nextNoteSet ?noteset4.\n" + 
				"\n" + 
				"\n" + 
				"	?noteset4 mso:hasNote ?note4.\n" + 
				"	?voice mso:hasNoteSet ?noteset4.\n" + 
				"	?note4 chord:natural note:A.\n" + 
				"    ?note4 mso:hasOctave \"4\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" + 
				"	?note4 chord:modifier chord:flat.    \n" + 
				"	?noteset4 mso:hasDuration ?duration4.\n" + 
				"	?duration4 a mso:32nd.\n" + 
				"	?noteset4 mso:nextNoteSet ?noteset5.    \n" + 
				"    \n" + 
				"    ?noteset5 mso:hasNote ?note5.\n" + 
				"	?voice mso:hasNoteSet ?noteset5.\n" + 
				"	?note5 chord:natural note:G.\n" + 
				"    ?note5 mso:hasOctave \"4\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" + 
				"	?noteset5 mso:hasDuration ?duration5.\n" + 
				"	?duration5 a mso:Eighth.\n" + 
				"	?noteset5 mso:nextNoteSet ?noteset6.\n" + 
				"    \n" + 
				"    ?noteset6 mso:hasNote ?note6.\n" + 
				"	?voice mso:hasNoteSet ?noteset6.\n" + 
				"	?note6 chord:natural note:Rest.    \n" + 
				"	?noteset6 mso:hasDuration ?duration6.\n" + 
				"	?duration5 a mso:Eighth.\n" + 
				"	?noteset6 mso:nextNoteSet ?noteset7.\n" + 
				"    \n" + 
				"    ?noteset7 mso:hasNote ?note7.\n" + 
				"	?voice mso:hasNoteSet ?noteset6.\n" + 
				"	?note7 chord:natural note:B.    \n" + 
				"    ?note7 chord:modifier chord:flat.    \n" + 
				"    ?note7 mso:hasOctave \"4\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" + 
				"	?noteset7 mso:hasDuration ?duration7.\n" + 
				"	?duration7 a mso:Eighth.\n" + 
				"    \n" + 
				"	FILTER ( NOT EXISTS {?note2 chord:modifier ?m2} )\n" + 
				"    FILTER ( NOT EXISTS {?note5 chord:modifier ?m5} )\n" + 
				"    FILTER ( NOT EXISTS {?note6 chord:modifier ?m6} )\n" + 
				"\n" + 
				"}"; 
		
		try (QueryExecution qexec = QueryExecutionFactory.create(sparql, model)) {
			ResultSet results = qexec.execSelect() ;
			for ( ; results.hasNext() ; )
			{
				QuerySolution soln = results.nextSolution() ;
				result = soln.get("?measure").toString();
				logger.info("Melody containing rests and dynamics in multiple durations: Measure " + result);
				
			}
		}

		assertEquals(result, "9");			
	}

}
