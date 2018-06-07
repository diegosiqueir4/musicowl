package de.wwu.music2rdf.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import de.wwu.music2rdf.core.Clef;
import de.wwu.music2rdf.core.Key;
import de.wwu.music2rdf.core.Measure;
import de.wwu.music2rdf.core.MusicScore;
import de.wwu.music2rdf.core.Note;
import de.wwu.music2rdf.core.ScorePart;
import de.wwu.music2rdf.core.Staff;
import de.wwu.music2rdf.core.Voice;
import de.wwu.music2rdf.util.Util;


public class MusicXML2RDF {

	private ArrayList<Clef> clefList;
	private ArrayList<Note> currentNotes;

	private boolean verbose = true;
	private String outputFile = "";
	private File inputFile = null;
	private String documentURI = "";
	private String documentTitle = "";
	private ArrayList<Staff> staves = new ArrayList<Staff>();
	private ArrayList<Voice> voices = new ArrayList<Voice>();
	private ArrayList<String> notesets = new ArrayList<String>();
	
	public MusicXML2RDF() {
		super();
		this.clefList = new ArrayList<Clef>();
		this.currentNotes = new ArrayList<Note>();

	}



	private void createRDF(MusicScore score){


		//TODO: Check score.getURI(), currently null. 

		StringBuffer ttl = new StringBuffer();
		String uid = UUID.randomUUID().toString();

		String rdfTypeURI = " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ";
		String rdfIdURI = " <http://www.w3.org/1999/02/22-rdf-syntax-ns#ID> ";
		String musicOWL = " <http://linkeddata.uni-muenster.de/ontology/musicscore#OBJECT> ";		
		String chordNoteOWL = " <http://purl.org/ontology/chord/note/OBJECT> ";
		String keyModeOWL = " <http://purl.org/ontology/tonality/mode/OBJECT> ";
		String chordOWL = " <http://purl.org/ontology/chord/OBJECT> ";
		String scoreURI = " <"+score.getURI()+"> ";
		String nodeURI = " <http://linkeddata.uni-muenster.de/node/"+uid+"/OBJECT> ";
		String musicOntology = " <http://purl.org/ontology/mo/OBJECT> "; 
		String dcOntology = " <http://purl.org/dc/elements/1.1/OBJECT> ";
		

		//TITLE MOVEMENT MISSING!
		
		
		for (int i = 0; i < score.getParts().size(); i++) {

			
			
			String partID = score.getParts().get(i).getId();
			String partObject = "";
			int notesetCounter = 0;
			int movementCounter = 0;
			//partID = score.getParts().get(i).getId();
			
//			partObject = nodeURI.replace("OBJECT","MOV" + movementCounter + "_" + "PART_" + partID);
//			ttl.append(partObject + rdfIdURI + "\"" +score.getParts().get(i).getId()  + "\" . \n");
			
			for (int j = 0; j < score.getParts().get(i).getMeasures().size(); j++) {
				
//				System.out.println("M > "+ score.getParts().get(i).getMeasures().get(j).getId());
//				for (int j2 = 0; j2 < score.getParts().get(i).getMeasures().get(j).getNotes().size(); j2++) {				 
//					System.out.println("  Note > "+score.getParts().get(i).getMeasures().get(j).getNotes().get(j2).getPitch() + " ("+score.getParts().get(i).getMeasures().get(j).getNotes().get(j2).isChord()+")" );					
//				}
				
				
				if(score.getParts().get(i).getMeasures().get(j).getId().equals("1")) {
				
					movementCounter++;
					
					partObject = nodeURI.replace("OBJECT","MOV" + movementCounter + "_" + "PART_" + partID);
					ttl.append(partObject + rdfIdURI + "\"" + partID  + "\" . \n");

					
					String movementObject = nodeURI.replace("OBJECT", "MOV" + movementCounter);
					ttl.append(scoreURI + musicOntology.replace("OBJECT", "movement") + movementObject + " .\n");
					ttl.append(movementObject + rdfTypeURI + musicOntology.replace("OBJECT", "Movement") + " .\n");
					
					if(score.getParts().get(i).getMeasures().get(j).getTitle() != null) {
						ttl.append(movementObject + dcOntology.replace("OBJECT", "title") + "\"" + score.getParts().get(i).getMeasures().get(j).getTitle() + "\" .\n");
					}
					
					ttl.append(movementObject + musicOWL.replace("OBJECT", "hasScorePart") + partObject + " . \n");		
					ttl.append(partObject + rdfTypeURI + musicOWL.replace("OBJECT", "ScorePart") + " .\n");
					ttl.append(partObject + dcOntology.replace("OBJECT", "description") + "\"" + score.getParts().get(i).getName() + "\" .\n");
					
				}
				
				String measureID = score.getParts().get(i).getMeasures().get(j).getId();
				String measureObject = nodeURI.replace("OBJECT", "MOV" + movementCounter + "_" + partID + "_M" + measureID);
				String keyObject = "";

				Key key = new Key();

				String instantObject = nodeURI.replace("OBJECT", "MOV" + movementCounter + "_" + "INSTANT_" + measureID); 

				ttl.append(instantObject + musicOWL.replace("OBJECT", "hasMeasure") + measureObject+ ".\n");
				ttl.append(instantObject + rdfTypeURI + musicOWL.replace("OBJECT", "Instant")+ ".\n");


				if(j>0){
					ttl.append(nodeURI.replace("OBJECT", "MOV" + movementCounter + "_" + partID + "_M" + score.getParts().get(i).getMeasures().get(j-1).getId()) + musicOWL.replace("OBJECT", "nextMeasure") + measureObject + ".\n");
					ttl.append(nodeURI.replace("OBJECT", "INSTANT_"+ score.getParts().get(i).getMeasures().get(j-1).getId()) + musicOWL.replace("OBJECT", "nextInstant") + instantObject + ".\n");
				}

				ttl.append(partObject + musicOWL.replace("OBJECT", "hasMeasure") + measureObject  + " . \n");
				//ttl.append(partObject + rdfIdURI + "\"" +score.getParts().get(i).getId()  + "\" . \n");
				ttl.append(measureObject + rdfTypeURI +  musicOWL.replace("OBJECT", "Measure")+ " . \n");
				ttl.append(measureObject + rdfIdURI + "\""+ score.getParts().get(i).getMeasures().get(j).getId() +"\" . \n");


				keyObject = nodeURI.replace("OBJECT", "MOV" + movementCounter + "_" + partID + "_M" + measureID + "_KEY");

				if(score.getParts().get(i).getMeasures().get(j).getKey().getMode()!=null){

					ttl.append(measureObject + musicOWL.replace("OBJECT", "hasKey") + keyObject + ". \n");
					ttl.append(keyObject + rdfTypeURI + " <http://purl.org/ontology/tonality/Key> . \n");

					if(score.getParts().get(i).getMeasures().get(j).getKey().getMode().equals("major")){

						key.setMode("major");

						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("0")) {
							//	keyType = "<http://purl.org/NET/c4dm/keys.owl#CMajor>";
							key.setTonic("C");
						}						
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("1")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#GMajor>";
							key.setTonic("G");
							//ttl.append(keyObject + " <http://www.w3.org/2002/07/owl#sameAs> <http://purl.org/ontology/tonality/key/Gmajor> . \n");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("2")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#DMajor>";
							key.setTonic("D");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("3")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#AMajor>";
							key.setTonic("A");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("4")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#EMajor>";
							key.setTonic("E");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("5")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#BMajor>";
							key.setTonic("B");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("6")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#FShparpMajor>";
							key.setTonic("Fs");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("7")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#CSharpMajor>";
							key.setTonic("Cs");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("-1")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#FMajor>";							
							key.setTonic("F");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("-2")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#BFlatMajor>";							
							key.setTonic("Bb");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("-3")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#EFlatMajor>";							
							key.setTonic("Eb");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("-4")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#AFlatMajor>";
							key.setTonic("Ab");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("-5")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#DFlatMajor>";
							key.setTonic("Db");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("-6")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#GFlatMajor>";							
							key.setTonic("Gb");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("-7")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#CFlatMajor>";
							key.setTonic("Cb");
						}

					}

					if(score.getParts().get(i).getMeasures().get(j).getKey().getMode().equals("minor")){

						key.setMode("minor");

						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("0")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#AMinor>";
							key.setTonic("A");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("1")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#EMinor>";
							key.setTonic("E");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("2")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#BMinor>";
							key.setTonic("B");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("3")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#FSharpMinor>";
							key.setTonic("Fs");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("4")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#CSharpMinor>";
							key.setTonic("Cs");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("5")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#GSharpMinor>";
							key.setTonic("Gs");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("6")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#DSharpMinor>";
							key.setTonic("Ds");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("7")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#AFlatMinor>";
							key.setTonic("Ab");
						}

						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("-1")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#DMinor>";
							key.setTonic("D");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("-2")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#GMinor>";
							key.setTonic("G");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("-3")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#CMinor>";
							key.setTonic("C");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("-4")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#FMinor>";
							key.setTonic("F");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("-5")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#BFlatMinor>";
							key.setTonic("Bb");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("-6")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#EFlatMinor>";
							key.setTonic("Eb");
						}
						if (score.getParts().get(i).getMeasures().get(j).getKey().getFifths().equals("-7")) {
							//keyType = "<http://purl.org/NET/c4dm/keys.owl#GSharpMinor>";
							key.setTonic("Gs");
						}

					}

					ttl.append(keyObject + " <http://purl.org/ontology/tonality/tonic> " + chordNoteOWL.replace("OBJECT", key.getTonic()) + " . \n");
					ttl.append(keyObject + " <http://purl.org/ontology/tonality/mode> " + keyModeOWL.replace("OBJECT", key.getMode()) + " . \n");

				} else {

					ttl.append(keyObject + rdfTypeURI + " <http://purl.org/ontology/tonality/Key> . \n");
				}


				String timeObject = nodeURI.replace("OBJECT","MOV" + movementCounter + "_" + partID + "_M" +measureID + "_TIME");

				ttl.append(measureObject + musicOWL.replace("OBJECT", "hasTime") + timeObject + ". \n");
				ttl.append(timeObject + rdfTypeURI + musicOWL.replace("OBJECT", "TimeSignature") + " . \n");
				ttl.append(timeObject + musicOWL.replace("OBJECT", "hasBeats") + "\"" + score.getParts().get(i).getMeasures().get(j).getTime().getBeats()+ "\"^^<http://www.w3.org/2001/XMLSchema#int> . \n");
				ttl.append(timeObject + musicOWL.replace("OBJECT", "hasBeatType") + "\"" + score.getParts().get(i).getMeasures().get(j).getTime().getBeatType()+ "\"^^<http://www.w3.org/2001/XMLSchema#int> . \n");

				if(score.getParts().get(i).getMeasures().get(j).getBarline()!=null){

					String barlineObject = nodeURI.replace("OBJECT","MOV" + movementCounter + "_" + partID + "_M" +measureID + "_REPEAT");
					ttl.append(measureObject + musicOWL.replace("OBJECT", "hasBarline") + barlineObject + ". \n" );

					if(score.getParts().get(i).getMeasures().get(j).getBarline().equals("backward")) ttl.append(barlineObject + rdfTypeURI + musicOWL.replace("OBJECT", "EndRepeat") + " . \n" );
					if(score.getParts().get(i).getMeasures().get(j).getBarline().equals("forward")) ttl.append(barlineObject + rdfTypeURI + musicOWL.replace("OBJECT", "BeginRepeat") + " . \n" );


				}

				String notesetObject = "";


				for (int k = 0; k < score.getParts().get(i).getMeasures().get(j).getNotes().size(); k++) {

					if(!score.getParts().get(i).getMeasures().get(j).getNotes().get(k).isChord()){

						notesetCounter++;

					}
					//System.out.println("M >" +score.getParts().get(i).getMeasures().get(j).getId() + " N >" + score.getParts().get(i).getMeasures().get(j).getNotes().size() );
					if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getStaff()==null){

						score.getParts().get(i).getMeasures().get(j).getNotes().get(k).setStaff("1");

					}

					notesetObject = nodeURI.replace("OBJECT", "MOV" + movementCounter + "_" + partID + "_M" + measureID + "_ST" + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getStaff() + "_V" + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getVoice().getId()  + "_NOTESET_" + notesetCounter) ;
					score.getParts().get(i).getMeasures().get(j).getNotes().get(k).setSignature(notesetObject);

					if(this.getPreviousNoteSet(score.getParts().get(i).getMeasures().get(j).getNotes().get(k)).getSignature()!=null){

						if(!this.getPreviousNoteSet(score.getParts().get(i).getMeasures().get(j).getNotes().get(k)).getSignature().equals(notesetObject)){

							ttl.append(this.getPreviousNoteSet(score.getParts().get(i).getMeasures().get(j).getNotes().get(k)).getSignature() + musicOWL.replace("OBJECT", "nextNoteSet") + notesetObject + " .\n");
							this.setCurrentNoteSet(score.getParts().get(i).getMeasures().get(j).getNotes().get(k));

						} 

					} else {

						this.setCurrentNoteSet(score.getParts().get(i).getMeasures().get(j).getNotes().get(k));

					}

					
					//x
					//ttl.append(measureObject + musicOWL.replace("OBJECT", "hasNoteSet") + notesetObject + ". \n");
					//x
					
					/**
					 * Staff 					
					 */
					
					Staff staff = new Staff();
					staff.setId(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getStaff());
					staff.setPart(score.getParts().get(i).getId());
					//System.out.println(staff.getId() + " | " + staff.getPart());
					boolean addStaff = true;
					
					for (int l = 0; l < staves.size(); l++) {
						
						if(staves.get(l).getId().equals(staff.getId()) && 
						   staves.get(l).getPart().equals(staff.getPart())) {
							addStaff = false;
							
						}
						
					}
					
					//String staffObject = "";
					String staffObject = nodeURI.replace("OBJECT", "MOV" + movementCounter + "_" + partID + "_STAFF_" + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getStaff());
					
					if(addStaff) {
						
						staves.add(staff);
						
						//staffObject = nodeURI.replace("OBJECT", partID + "_STAFF_" + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getStaff());
						
						ttl.append(staffObject + rdfTypeURI + musicOWL.replace("OBJECT", "Staff") + " . \n");
						ttl.append(partObject + musicOWL.replace("OBJECT", "hasStaff") + staffObject +" . \n");
						//TODO: Remove staff hasNoteSet noteset
						//ttl.append(staffObject + musicOWL.replace("OBJECT", "hasNoteSet") + notesetObject + ". \n");

						ttl.append(staffObject + rdfIdURI + "\""+ score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getStaff() + "\" . \n");
						
						//System.out.println(partObject + musicOWL.replace("OBJECT", "hasStaff") + staffObject +" . \n");
					}
					

					
					/**
					 * Voice
					 */
					
					String clefObject ="";
					String voiceObject = "";
					//String durationObject = "";
					String durationObject = nodeURI.replace("OBJECT", "MOV" + movementCounter + "_" + partID + "_M" + measureID + "_NS" + notesetCounter + "_DURATION");
					Voice voice = new Voice();
					voice.setPart(partID);
					
					if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getVoice()==null){

						voiceObject = nodeURI.replace("OBJECT","MOV" + movementCounter + "_" + partID + "_VOICE_1");
						
						//ttl.append(voiceObject + rdfIdURI + "\"1\" . \n");
						
						voice.setId("1");
						
						
						score.getParts().get(i).getMeasures().get(j).getNotes().get(k).setVoice(voice);
						
					} else {

						voiceObject = nodeURI.replace("OBJECT","MOV" + movementCounter + "_" + partID + "_VOICE_" + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getVoice().getId());
						voice.setId(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getVoice().getId());
						//ttl.append(voiceObject + rdfIdURI + "\""+score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getVoice().getId() + "\" . \n");
					}

					
					boolean addVoice = true;
					
					for (int l = 0; l < voices.size(); l++) {
						
						if(voices.get(l).getId().equals(voice.getId()) && 
						   voices.get(l).getPart().equals(voice.getPart())) {
							addVoice = false;
							
						}
						
					}
					
					if(addVoice) {
						
						voices.add(voice);
						ttl.append(voiceObject + rdfIdURI + "\"" + voice.getId() +"\" . \n");
						ttl.append(voiceObject + rdfTypeURI + musicOWL.replace("OBJECT", "Voice") + " . \n");										
						ttl.append(staffObject + musicOWL.replace("OBJECT", "hasVoice") + voiceObject + " . \n");
						
					}
					
					
					boolean addNoteSet = true;
					
					for (int l = 0; l < notesets.size(); l++) {
						
						if(notesets.get(l).equals(notesetObject)) {
							addNoteSet = false;
						}
						
					}
					
					if(addNoteSet) {
						
						notesets.add(notesetObject);
						ttl.append(voiceObject + musicOWL.replace("OBJECT", "hasNoteSet") + notesetObject + ". \n");
						ttl.append(notesetObject + rdfTypeURI + musicOWL.replace("OBJECT", "NoteSet") + ". \n");
						
						clefObject = nodeURI.replace("OBJECT", "MOV" + movementCounter + "_" + partID + "_M" + measureID + "_NS_" + notesetCounter + "_CLEF");	
						ttl.append(notesetObject + musicOWL.replace("OBJECT", "hasClef") + clefObject + ". \n");
						
						ttl.append(measureObject + musicOWL.replace("OBJECT", "hasNoteSet") + notesetObject + ". \n");
						
						
						if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef()!=null){

//							String clefObject = nodeURI.replace("OBJECT", "MOV" + movementCounter + "_" + partID + "_M" + measureID + "_NS_" + notesetCounter + "_CLEF");	
//							ttl.append(notesetObject + musicOWL.replace("OBJECT", "hasClef") + clefObject + ". \n");


							for (int l = 0; l < score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getArticulations().size(); l++) {

								String articulationObject = nodeURI.replace("OBJECT", "MOV" + movementCounter + "_" + partID + "_M" + measureID + "_NS" + notesetCounter + "_ARTICULATION_" + l);

								ttl.append(notesetObject + musicOWL.replace("OBJECT", "hasArticulation") + articulationObject + ".\n");
								ttl.append(articulationObject + rdfTypeURI + musicOWL.replace("OBJECT", this.getCapital(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getArticulations().get(l))) + ".\n");													

							}

							if(!score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("percussion")){


								if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("3") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("C")){

									ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Alto")+ ".\n");

								}

								if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("5") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("C")){

									ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Baritone")+ ".\n");						

								}

								if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("3") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("F")){

									ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Baritone")+ ".\n");					

								}


								if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("4") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("F")){

									ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Bass") + ".\n");					

								}

								if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("1") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("G")){

									ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "FrenchViolin") + ".\n");				

								}

								if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("2") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("C")){

									ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "MezzoSoprano") + ".\n");		

								}

								if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("1") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("C")){

									ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Soprano") + ".\n");		

								}

								if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("5") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("F")){

									ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "SubBass") + ".\n");	

								}

								if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("4") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("C")){

									ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Tenor") + ".\n");

								}

								if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("2") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("G")){

									ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Trebble") + ".\n");

								}

								if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("percussion")){

									ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Percussion") + ".\n");
								}

							} else {

								ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Percussion")+ ".\n");

							}

						}
						
						
						//durationObject = nodeURI.replace("OBJECT", "MOV" + movementCounter + "_" + partID + "_M" + measureID + "_NS" + notesetCounter + "_DURATION");
						ttl.append(notesetObject + musicOWL.replace("OBJECT", "hasDuration") + durationObject + ".\n");

						if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getType()==null){

							ttl.append(durationObject + rdfTypeURI + musicOWL.replace("OBJECT", "Duration") + ".\n");

						} else {

							ttl.append(durationObject + rdfTypeURI + musicOWL.replace("OBJECT", this.getCapital(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getType()))+ ".\n");

						}
						
					}
										
					
					/**
					 * Note
					 */
					

					String noteObject = nodeURI.replace("OBJECT", "MOV" + movementCounter + "_" + partID + "_M" + measureID + "_NS_" + notesetCounter + "_NOTE_" + k);

					ttl.append(notesetObject + musicOWL.replace("OBJECT", "hasNote") + noteObject + ".\n");
					ttl.append(noteObject + rdfTypeURI + chordOWL.replace("OBJECT", "Note") + ".\n");

					if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch()==null) {

						ttl.append(noteObject + chordOWL.replace("OBJECT", "natural") + chordOWL.replace("OBJECT", "note/Rest") + ".\n");

					} else { 

						ttl.append(noteObject + chordOWL.replace("OBJECT", "natural") + chordOWL.replace("OBJECT", "note/" + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch()) + ".\n");

					}


					if(!score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getAccidental().equals("") &&
							!score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getAccidental().equals("natural")){

						ttl.append(noteObject + chordOWL.replace("OBJECT", "modifier") + chordOWL.replace("OBJECT", score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getAccidental()) + ".\n");

					} 


					/**
					 * Add sharps and flats to notes depending on the key. MuaicXML relies on the reader to identify the accidentals depending on the keys. 
					 */

					if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getAccidental().equals("") &&
							score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch() != null){						

						//if(keyType.equals("<http://purl.org/NET/c4dm/keys.owl#AMinor>") || keyType.equals("<http://purl.org/NET/c4dm/keys.owl#CMajor>")){
						if((key.getTonic().equals("A") && key.getMode().equals("minor")) || (key.getTonic().equals("C") && key.getMode().equals("major"))){

							//ttl.append(noteObject + chordOWL.replace("OBJECT", "modifier") + chordOWL.replace("OBJECT", score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getAccidental()) + ".\n");

						}

						//if(keyType.equals("<http://purl.org/NET/c4dm/keys.owl#GMajor>") || keyType.equals("<http://purl.org/NET/c4dm/keys.owl#EMinor>")){
						if((key.getTonic().equals("G") && key.getMode().equals("major")) || (key.getTonic().equals("E") && key.getMode().equals("minor"))){

							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("F")){

								ttl.append(noteObject + chordOWL.replace("OBJECT", "modifier") + chordOWL.replace("OBJECT", "sharp") + ".\n");

							}

						}

						//if(keyType.equals("<http://purl.org/NET/c4dm/keys.owl#DMajor>") || keyType.equals("<http://purl.org/NET/c4dm/keys.owl#BMinor>")){
						if((key.getTonic().equals("D") && key.getMode().equals("major")) || (key.getTonic().equals("B") && key.getMode().equals("minor"))){

							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("F") || 
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("C")){

								ttl.append(noteObject + chordOWL.replace("OBJECT", "modifier") + chordOWL.replace("OBJECT", "sharp") + ".\n");

							}

						}

						//if(keyType.equals("<http://purl.org/NET/c4dm/keys.owl#AMajor>") || keyType.equals("<http://purl.org/NET/c4dm/keys.owl#FMinor>")){
						if((key.getTonic().equals("A") && key.getMode().equals("major")) || (key.getTonic().equals("F") && key.getMode().equals("minor"))){
							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("F") || 
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("C") || 
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("G")){

								ttl.append(noteObject + chordOWL.replace("OBJECT", "modifier") + chordOWL.replace("OBJECT", "sharp") + ".\n");

							} 

						}

						//if(keyType.equals("<http://purl.org/NET/c4dm/keys.owl#EMajor>") || keyType.equals("<http://purl.org/NET/c4dm/keys.owl#CMinor>")){
						if((key.getTonic().equals("E") && key.getMode().equals("major")) || (key.getTonic().equals("C") && key.getMode().equals("minor"))){
							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("F") || 
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("C") || 
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("G") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("D")){

								ttl.append(noteObject + chordOWL.replace("OBJECT", "modifier") + chordOWL.replace("OBJECT", "sharp") + ".\n");

							}

						}

						//if(keyType.equals("<http://purl.org/NET/c4dm/keys.owl#BMajor>") || keyType.equals("<http://purl.org/NET/c4dm/keys.owl#GMinor>")){
						if((key.getTonic().equals("B") && key.getMode().equals("major")) || (key.getTonic().equals("G") && key.getMode().equals("minor"))){							
							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("F") || 
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("C") || 
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("G") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("D") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("A")){

								ttl.append(noteObject + chordOWL.replace("OBJECT", "modifier") + chordOWL.replace("OBJECT", "sharp") + ".\n");

							}

						}

						//if(keyType.equals("<http://purl.org/NET/c4dm/keys.owl#FSharpMajor>") || keyType.equals("<http://purl.org/NET/c4dm/keys.owl#DSharpMinor>")){
						if((key.getTonic().equals("Fs") && key.getMode().equals("major")) || (key.getTonic().equals("Ds") && key.getMode().equals("minor"))){
							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("F") || 
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("C") || 
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("G") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("D") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("A") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("E")){

								ttl.append(noteObject + chordOWL.replace("OBJECT", "modifier") + chordOWL.replace("OBJECT", "sharp") + ".\n");

							}

						}

						//if(keyType.equals("<http://purl.org/NET/c4dm/keys.owl#CSharpMajor>") || keyType.equals("<http://purl.org/NET/c4dm/keys.owl#ASharpMinor>")){
						if((key.getTonic().equals("Cs") && key.getMode().equals("major")) || (key.getTonic().equals("As") && key.getMode().equals("minor"))){
							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("F") || 
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("C") || 
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("G") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("D") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("A") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("E") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("B")){

								ttl.append(noteObject + chordOWL.replace("OBJECT", "modifier") + chordOWL.replace("OBJECT", "sharp") + ".\n");

							}

						}


						//if(keyType.equals("<http://purl.org/NET/c4dm/keys.owl#FMajor>") || keyType.equals("<http://purl.org/NET/c4dm/keys.owl#DMinor>")){
						if((key.getTonic().equals("F") && key.getMode().equals("major")) || (key.getTonic().equals("D") && key.getMode().equals("minor"))){
							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("B")){

								ttl.append(noteObject + chordOWL.replace("OBJECT", "modifier") + chordOWL.replace("OBJECT", "flat") + ".\n");

							}

						}


						//if(keyType.equals("<http://purl.org/NET/c4dm/keys.owl#BFlatMajor>") || keyType.equals("<http://purl.org/NET/c4dm/keys.owl#GMinor>")){
						if((key.getTonic().equals("Bb") && key.getMode().equals("major")) || (key.getTonic().equals("G") && key.getMode().equals("minor"))){
							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("B") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("E")){

								ttl.append(noteObject + chordOWL.replace("OBJECT", "modifier") + chordOWL.replace("OBJECT", "flat") + ".\n");

							}

						}

						//if(keyType.equals("<http://purl.org/NET/c4dm/keys.owl#EFlatMajor>") || keyType.equals("<http://purl.org/NET/c4dm/keys.owl#CMinor>")){
						if((key.getTonic().equals("Eb") && key.getMode().equals("major")) || (key.getTonic().equals("C") && key.getMode().equals("minor"))){
							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("B") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("E") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("A")) {

								ttl.append(noteObject + chordOWL.replace("OBJECT", "modifier") + chordOWL.replace("OBJECT", "flat") + ".\n");

							}

						}

						//if(keyType.equals("<http://purl.org/NET/c4dm/keys.owl#AFlatMajor>") || keyType.equals("<http://purl.org/NET/c4dm/keys.owl#FMinor>")){
						if((key.getTonic().equals("Ab") && key.getMode().equals("major")) || (key.getTonic().equals("F") && key.getMode().equals("minor"))){
							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("B") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("E") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("A") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("D")) {

								ttl.append(noteObject + chordOWL.replace("OBJECT", "modifier") + chordOWL.replace("OBJECT", "flat") + ".\n");

							}

						}

						//if(keyType.equals("<http://purl.org/NET/c4dm/keys.owl#DFlatMajor>") || keyType.equals("<http://purl.org/NET/c4dm/keys.owl#BFlatMinor>")){
						if((key.getTonic().equals("Db") && key.getMode().equals("major")) || (key.getTonic().equals("Bb") && key.getMode().equals("minor"))){
							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("B") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("E") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("A") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("D") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("G")) {

								ttl.append(noteObject + chordOWL.replace("OBJECT", "modifier") + chordOWL.replace("OBJECT", "flat") + ".\n");

							}

						}

						//if(keyType.equals("<http://purl.org/NET/c4dm/keys.owl#GFlatMajor>") || keyType.equals("<http://purl.org/NET/c4dm/keys.owl#EFlatMinor>")){
						if((key.getTonic().equals("Gb") && key.getMode().equals("major")) || (key.getTonic().equals("Eb") && key.getMode().equals("minor"))){
							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("B") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("E") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("A") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("D") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("G") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("C")) {

								ttl.append(noteObject + chordOWL.replace("OBJECT", "modifier") + chordOWL.replace("OBJECT", "flat") + ".\n");

							}

						}

						//if(keyType.equals("<http://purl.org/NET/c4dm/keys.owl#CFlatMajor>") || keyType.equals("<http://purl.org/NET/c4dm/keys.owl#AFlatMinor>")){
						if((key.getTonic().equals("Cb") && key.getMode().equals("major")) || (key.getTonic().equals("Ab") && key.getMode().equals("minor"))){
							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("B") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("E") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("A") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("D") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("G") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("C") ||
									score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch().equals("F")) {

								ttl.append(noteObject + chordOWL.replace("OBJECT", "modifier") + chordOWL.replace("OBJECT", "flat") + ".\n");

							}

						}
						
					}


					
					
					
					
					
					


					if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getOctave()!=null){

						ttl.append(noteObject + musicOWL.replace("OBJECT", "hasOctave") + "\""+ score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getOctave()+ "\"^^<http://www.w3.org/2001/XMLSchema#int> . \n");	

					}



					
					if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).isDot()){
						String dotObject = nodeURI.replace("OBJECT", "MOV" + movementCounter + "_" +partID + "_M" + measureID + "_NS" + notesetCounter + "_N" + k + "_D_DOT");
						ttl.append(durationObject + musicOWL.replace("OBJECT", "hasDurationAttribute") + dotObject + ".\n" );
						ttl.append(dotObject + rdfTypeURI + musicOWL.replace("OBJECT", "Dot") + ".\n");
					}


//					if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getType()==null){
//
//						ttl.append(durationObject + rdfIdURI + musicOWL.replace("OBJECT", "rest")+ ".\n");
//
//					}



					
					


					for (int l = 0; l < score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getDynamics().size(); l++) {

						String dynamicObject = nodeURI.replace("OBJECT", "MOV" + movementCounter + "_" + partID + "_M" + measureID + "_NS" + notesetCounter + "_DYNAMIC_" + l);
						ttl.append(dynamicObject + rdfTypeURI + musicOWL.replace("OBJECT", score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getDynamics().get(l)) + ".\n");
						ttl.append(notesetObject + musicOWL.replace("OBJECT", "hasDynamic") + dynamicObject + ".\n");

					}




//					if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef()!=null){
//
////						String clefObject = nodeURI.replace("OBJECT", "MOV" + movementCounter + "_" + partID + "_M" + measureID + "_NS_" + notesetCounter + "_CLEF");	
////						ttl.append(notesetObject + musicOWL.replace("OBJECT", "hasClef") + clefObject + ". \n");
//
//
//						for (int l = 0; l < score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getArticulations().size(); l++) {
//
//							String articulationObject = nodeURI.replace("OBJECT", partID + "_M" + measureID + "_NS" + notesetCounter + "_ARTICULATION_" + l);
//
//							ttl.append(notesetObject + musicOWL.replace("OBJECT", "hasArticulation") + articulationObject + ".\n");
//							ttl.append(articulationObject + rdfTypeURI + musicOWL.replace("OBJECT", this.getCapital(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getArticulations().get(l))) + ".\n");													
//
//						}
//
//						if(!score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("percussion")){
//
//
//							if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("3") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("C")){
//
//								ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Alto")+ ".\n");
//
//							}
//
//							if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("5") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("C")){
//
//								ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Baritone")+ ".\n");						
//
//							}
//
//							if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("3") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("F")){
//
//								ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Baritone")+ ".\n");					
//
//							}
//
//
//							if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("4") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("F")){
//
//								ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Bass") + ".\n");					
//
//							}
//
//							if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("1") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("G")){
//
//								ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "FrenchViolin") + ".\n");				
//
//							}
//
//							if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("2") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("C")){
//
//								ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "MezzoSoprano") + ".\n");		
//
//							}
//
//							if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("1") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("C")){
//
//								ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Soprano") + ".\n");		
//
//							}
//
//							if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("5") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("F")){
//
//								ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "SubBass") + ".\n");	
//
//							}
//
//							if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("4") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("C")){
//
//								ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Tenor") + ".\n");
//
//							}
//
//							if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine().equals("2") && score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("G")){
//
//								ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Trebble") + ".\n");
//
//							}
//
//							if (score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign().equals("percussion")){
//
//								ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Percussion") + ".\n");
//							}
//
//						} else {
//
//							ttl.append(clefObject + rdfTypeURI + musicOWL.replace("OBJECT", "Percussion")+ ".\n");
//
//						}
//
//					}

				}

			}
			
		}


		try {

			String xml = new String(score.getFileContent().getBytes(StandardCharsets.UTF_8));
			xml = xml.replace("\\", "\\\\");
			ttl.append(scoreURI + musicOWL.replace("OBJECT", "asMusicXML")+ "\"" + xml.replace("\"", "'") + "\" ." );
			
			FileOutputStream fileStream = new FileOutputStream(new File(this.getOutputFile()),false);
			OutputStreamWriter writer = new OutputStreamWriter(fileStream, StandardCharsets.UTF_8);

			writer.append(ttl.toString());
			writer.close();		

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	public void parseMusicXML(){

		String musicXMLString ="";

		File file = this.getInputFile();
		
		try {

			System.out.println("\nProcessing " + file.getName() + " ... \n");
			Date start = new Date();

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setValidating(false);
			DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

			builder.setEntityResolver(new EntityResolver() {

				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {

					return new InputSource(new StringReader(""));

				}
			});

			Document document = builder.parse(file);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			musicXMLString = writer.getBuffer().toString().replaceAll("\n|\r", "");

			System.out.println("Loading XML file: " + Util.timeElapsed(start, new Date()));
			
		} catch (Exception e) {

			System.err.println("[Error processing MusicXML File]: " + e.getMessage());
			
		}	
		
		
		Date start = new Date();
		MusicScore score = createMusicScoreDocument(musicXMLString); 
		score.setFileContent(musicXMLString);
		score.setOutputFileName(file.getName());
		score.setURI(this.getDocumentURI());

//		System.out.println("\nCreating RDF file for " + file.getName() + ", please wait ...");
		System.out.println("Creating MusicScore object: "+Util.timeElapsed(start, new Date()));
		
		start = new Date();
		this.createRDF(score);
		System.out.println("Score serialization: "+Util.timeElapsed(start, new Date()));
		//return score;

	}



	private MusicScore createMusicScoreDocument(String musicXML){

		MusicScore score = new MusicScore();



		String currentMode = "";
		String currentFifth = ""; 
		String currentBeatType = "";
		String currentBeat = "";
		int slurCount = 0;
		boolean slurFlag = false;

		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setValidating(false);
			DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

			builder.setEntityResolver(new EntityResolver() {

				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {

					return new InputSource(new StringReader(""));

				}
			});


			InputSource is = new InputSource(new StringReader(musicXML));
			Document document = builder.parse(is);

			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();

			NodeList subfields = (NodeList) xpath.evaluate("//work/work-title", document,XPathConstants.NODESET);

			if (subfields.getLength() != 0) {

				score.setTitle(subfields.item(0).getTextContent());

			}

			subfields = (NodeList) xpath.evaluate("//score-partwise/part-list/score-part/@id", document,XPathConstants.NODESET);

			for (int i = 0; i < subfields.getLength(); i++) {

				ScorePart part = new ScorePart();
				part.setId(subfields.item(i).getTextContent());
				score.getParts().add(part);					

			}


			for (int i = 0; i < score.getParts().size(); i++) {

				this.clefList = new ArrayList<Clef>();
				
				NodeList nodePartName = (NodeList) xpath.evaluate("//score-partwise/part-list/score-part[@id='"+score.getParts().get(i).getId()+"']/part-name", document,XPathConstants.NODESET);

				if(nodePartName.getLength()!=0){

					if(nodePartName.item(0).getTextContent().trim().equals("")){

						score.getParts().get(i).setName(score.getParts().get(i).getId());

					} else {

						score.getParts().get(i).setName(nodePartName.item(0).getTextContent());

					}

				} else {

					score.getParts().get(i).setName(score.getParts().get(i).getId());

				}
				
				NodeList nodeMeasures = (NodeList) xpath.evaluate("//score-partwise/part[@id='"+score.getParts().get(i).getId()+"']/measure", document,XPathConstants.NODESET);

				if (nodeMeasures.getLength() != 0) {


					for (int j = 0; j < nodeMeasures.getLength(); j++) {

						Measure measure = new Measure();
						measure.setId(nodeMeasures.item(j).getAttributes().getNamedItem("number").getNodeValue());	
						
						//System.out.println(nodeMeasures.item(j).getTextContent());
						
						if(measure.getId().equals("1")) {
							
							//NodeList nodeMovementTitle = (NodeList) xpath.evaluate("//score-partwise/part[@id='" + score.getParts().get(i).getId() + "']/measure[@number='"+measure.getId()+"']/direction/direction-type/words", nodeMeasures.item(j),XPathConstants.NODESET);
							//NodeList nodeMovementTitle = (NodeList) xpath.evaluate("//direction-type/words", nodeMeasures.item(j),XPathConstants.NODESET);
							
							Element eElement = (Element) nodeMeasures.item(j);
							
							//if(nodeMovementTitle.getLength()!=0){
							//System.out.println(eElement.getElementsByTagName("words2").getLength());
								//measure.setTitle(nodeMovementTitle.item(0).getTextContent());
							if(eElement.getElementsByTagName("words").getLength() != 0) {
								measure.setTitle(eElement.getElementsByTagName("words").item(0).getTextContent());
								System.out.println("["+ score.getParts().get(i).getName() + "] Processing movement > " + measure.getTitle() +" ... ");
							}
							//}
						}
						
						
						
						score.getParts().get(i).getMeasures().add(measure);

						/**
						 * Key
						 */

						NodeList nodeMeasureKeyMode = (NodeList) xpath.evaluate("//attributes/key/mode", nodeMeasures.item(j),XPathConstants.NODESET);
						//System.out.println(nodeMeasureKeyMode.item(0).getTextContent());
						if(nodeMeasureKeyMode.getLength()!=0){

							score.getParts().get(i).getMeasures().get(j).getKey().setMode(nodeMeasureKeyMode.item(0).getTextContent());
							currentMode = nodeMeasureKeyMode.item(0).getTextContent();

						} else {

							score.getParts().get(i).getMeasures().get(j).getKey().setMode(currentMode);
						}

						NodeList nodeMeasureKeyFifth = (NodeList) xpath.evaluate("//attributes/key/fifths", nodeMeasures.item(j),XPathConstants.NODESET);

						if(nodeMeasureKeyFifth.getLength()!=0){

							score.getParts().get(i).getMeasures().get(j).getKey().setFifths(nodeMeasureKeyFifth.item(0).getTextContent());
							currentFifth = nodeMeasureKeyFifth.item(0).getTextContent();
							

						} else {

							score.getParts().get(i).getMeasures().get(j).getKey().setFifths(currentFifth);
							
						}





						/**
						 * one xpath element 4 all
						 */

						
						//NodeList nodeMeasureElementsList = nodeMeasures.item(j).getChildNodes();
						//System.out.println(nodeMeasures.item(j).getChildNodes().item(3).getTextContent() +"\n");
						//NodeList nodeMeasureElementsList = (NodeList) xpath.evaluate("//score-partwise/part[@id='"+score.getParts().get(i).getId()+"']/measure[@number='"+measure.getId()+"']", document,XPathConstants.NODESET);
						
						NodeList nodeMeasureElementsList = nodeMeasures;
						
						//System.out.println(nodeMeasures.item(104).getTextContent());
						
						ArrayList<String> dynamics = new ArrayList<String>();

						///////////
						for (int k = 0; k < nodeMeasureElementsList.item(0).getChildNodes().getLength(); k++) {
						/////////////
//						for (int k = 0; k < nodeMeasureElementsList.getLength(); k++) {
								
							Note note = new Note();

							if(nodeMeasureElementsList.item(0).getChildNodes().item(k).getNodeName().equals("direction")){

								Element elementDirection = (Element) nodeMeasureElementsList.item(0).getChildNodes().item(k);

								for (int l = 0; l < elementDirection.getChildNodes().getLength(); l++) {

									if(elementDirection.getElementsByTagName("dynamics").item(l)!=null){

										NodeList listDynamics = elementDirection.getElementsByTagName("dynamics").item(0).getChildNodes();

										for (int m = 0; m < listDynamics.getLength(); m++) {

											if(!listDynamics.item(m).getNodeName().matches("#text")) {

												dynamics.add(listDynamics.item(m).getNodeName());

											}
										}

									}

								}


							}



							if(nodeMeasureElementsList.item(0).getChildNodes().item(k).getNodeName().equals("attributes")){

								NodeList listAttributes = nodeMeasureElementsList.item(0).getChildNodes().item(k).getChildNodes();

								for (int l = 0; l < listAttributes.getLength(); l++) {


									if (listAttributes.item(l).getNodeName().equals("clef")) {

										NodeList listClef = listAttributes.item(l).getChildNodes();

										Clef clef = new Clef();

										for (int m = 0; m < listClef.getLength(); m++) {


											if(listAttributes.item(l).getAttributes().getNamedItem("number")!=null) {

												clef.setStaffNumber(listAttributes.item(l).getAttributes().getNamedItem("number").getNodeValue());


											} else {

												clef.setStaffNumber("1");

											}


											if(listClef.item(m).getNodeName().equals("line")){

												clef.setLine(listClef.item(m).getTextContent());

											}

											if(listClef.item(m).getNodeName().equals("sign")){

												clef.setSign(listClef.item(m).getTextContent());

											}


										}

										this.addClef(clef);										

									}



									if(listAttributes.item(l).getNodeName().equals("time")){

										NodeList listTime = listAttributes.item(l).getChildNodes();

										for (int m = 0; m < listTime.getLength(); m++) {

											if (listTime.item(m).getNodeName().equals("beats")){

												currentBeat=listTime.item(m).getTextContent();

											}

											if (listTime.item(m).getNodeName().equals("beat-type")){

												currentBeatType = listTime.item(m).getTextContent();


											}

										}


									}


								}

							}


							measure.getTime().setBeats(currentBeat);
							measure.getTime().setBeatType(currentBeatType);


							if(nodeMeasureElementsList.item(0).getChildNodes().item(k).getNodeName().equals("barline")){

								NodeList listBarline = nodeMeasureElementsList.item(0).getChildNodes().item(k).getChildNodes();

								for (int l = 0; l < listBarline.getLength(); l++) {

									if (listBarline.item(l).getNodeName().equals("repeat")){

										measure.setBarline(listBarline.item(l).getAttributes().getNamedItem("direction").getNodeValue());

									}

								}

							}


							if(nodeMeasureElementsList.item(0).getChildNodes().item(k).getNodeName().equals("note")){


								NodeList listNoteElements = nodeMeasureElementsList.item(0).getChildNodes().item(k).getChildNodes();

								//System.out.println(nodeMeasureElementsList.item(0).getChildNodes().item(k).getTextContent());								
																
								for (int l = 0; l < listNoteElements.getLength(); l++) {

									if (listNoteElements.item(l).getNodeName().equals("staff")) {

										note.setStaff(listNoteElements.item(l).getTextContent());

									}

									if (listNoteElements.item(l).getNodeName().equals("notations")) {

										NodeList listNotationElements = listNoteElements.item(l).getChildNodes();

										for (int m = 0; m < listNotationElements.getLength(); m++) {

											if(listNotationElements.item(m).getNodeName().equals("slur")){

												if(listNotationElements.item(m).getAttributes().getNamedItem("type").getNodeValue().equals("start")){

													slurCount = slurCount + 1; 
													slurFlag = true;													

												}	

												if(listNotationElements.item(m).getAttributes().getNamedItem("type").getNodeValue().equals("stop")){

													slurFlag = false;
												}

												note.setSlur(Integer.toString(slurCount));

											}



											if(listNotationElements.item(m).getNodeName().equals("articulations")){

												NodeList listAriculationElements = listNotationElements.item(m).getChildNodes();	

												for (int n = 0; n < listAriculationElements.getLength(); n++) {

													if(!listAriculationElements.item(n).getNodeName().matches("#text")){

														note.getArticulations().add(listAriculationElements.item(n).getNodeName());

													}

												}


											}


										}


									}


								}


								Element elementNotes = (Element) nodeMeasureElementsList.item(0).getChildNodes().item(k);


								if(elementNotes.getElementsByTagName("chord").item(0)!=null) note.setChord(true);
								if(elementNotes.getElementsByTagName("dot").item(0)!=null) note.setDot(true);
								if(elementNotes.getElementsByTagName("octave").item(0)!=null) note.setOctave(elementNotes.getElementsByTagName("octave").item(0).getTextContent());
								if(elementNotes.getElementsByTagName("step").item(0)!=null) note.setPitch(elementNotes.getElementsByTagName("step").item(0).getTextContent());								
								if(elementNotes.getElementsByTagName("type").item(0)!=null) note.setType(elementNotes.getElementsByTagName("type").item(0).getTextContent());

								if(elementNotes.getElementsByTagName("voice").item(0)!=null) {

									note.getVoice().setId(elementNotes.getElementsByTagName("voice").item(0).getTextContent());

								} else {

									note.getVoice().setId("1");

								}

								note.getVoice().setPart(score.getParts().get(i).getId());
								note.getVoice().setMeasure(measure.getId());


								if(elementNotes.getElementsByTagName("accidental").item(0)!=null){

									note.setAccidental(elementNotes.getElementsByTagName("accidental").item(0).getTextContent());

								}


								if(dynamics.size()!=0) {

									for (int l = 0; l < dynamics.size(); l++) {

										note.getDynamics().add(dynamics.get(l));
									}

									dynamics = new ArrayList<String>();
								}

								if(note.getStaff()==null)note.setStaff("1");


								note.getClef().setLine(this.getCurrentClef(note.getStaff()).getLine());
								note.getClef().setSign(this.getCurrentClef(note.getStaff()).getSign());


								if(slurFlag)note.setSlur(Integer.toString(slurCount));


								score.getParts().get(i).getMeasures().get(j).getNotes().add(note);


							}

						}


					}

				}

			}







			if(verbose){

				for (int i = 0; i < score.getParts().size(); i++) {

					System.out.println("Part: " + score.getParts().get(i).getId());		

					for (int j = 0; j < score.getParts().get(i).getMeasures().size(); j++) {

						System.out.println("|-- Measure ("+score.getParts().get(i).getId()+"): " + score.getParts().get(i).getMeasures().get(j).getId());
						System.out.println("	|-- Key mode: " + score.getParts().get(i).getMeasures().get(j).getKey().getMode());
						System.out.println("	|-- Key fifth: " + score.getParts().get(i).getMeasures().get(j).getKey().getFifths());
						System.out.println("	|-- Time beats: " + score.getParts().get(i).getMeasures().get(j).getTime().getBeats());
						System.out.println("	|-- Time beat-type: " + score.getParts().get(i).getMeasures().get(j).getTime().getBeatType());
						System.out.println("	|-- Bar Line: " + score.getParts().get(i).getMeasures().get(j).getBarline());

						for (int k = 0; k < score.getParts().get(i).getMeasures().get(j).getNotes().size(); k++) {

							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).isDot()) System.out.println("		|-- Dot");
							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).isChord()) System.out.println("		|-- Chord");
							System.out.println("		|-- Note-Step   	: " + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getPitch());
							System.out.println("		|-- Note-Octave 	: " + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getOctave());
							System.out.println("		|-- Staff  		: " + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getStaff());
							System.out.println("		|-- Voice  		: " + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getVoice().getId() + " " + score.getParts().get(i).getId() +  " M" + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getVoice().getMeasure());
							System.out.println("		|-- Type  		: " + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getType());
							System.out.println("		|-- Clef-Sign  		: " + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getSign());
							System.out.println("		|-- Clef-Line  		: " + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getClef().getLine());

							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getSlur() != null)
								System.out.println("		|-- Slur  		: " + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getSlur());

							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getDynamics().size()!=0){

								for (int l = 0; l < score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getDynamics().size(); l++) {

									System.out.println("		|-- Dynamic  		: " + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getDynamics().get(l));

								}

							}

							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getArticulations().size()!=0){

								for (int l = 0; l < score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getArticulations().size(); l++) {

									System.out.println("		|-- Articulations  	: " + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getArticulations().get(l));

								}

							}

							if(score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getAccidental()!=null) {
								System.out.println("		|-- Accidental  	: " + score.getParts().get(i).getMeasures().get(j).getNotes().get(k).getAccidental());}
							System.out.println("		|-----------------------|");
						}

					}
				}


			}

//			Date date= new Date();
//			long time = date.getTime();
//			Timestamp ts = new Timestamp(time);
//			System.out.println("Finished at: "+ts + "\n");


		} catch (SAXException  e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return score; 

	}



	private void addClef(Clef clef){

		boolean exists = false;

		for (int i = 0; i < clefList.size(); i++) {

			if(clefList.get(i).getStaffNumber().equals(clef.getStaffNumber())){

				clefList.get(i).setLine(clef.getLine());
				clefList.get(i).setSign(clef.getSign());
				exists = true;

			}

		}


		if(!exists) 
			clefList.add(clef);


	}

	private Clef getCurrentClef(String staff){

		Clef result = new Clef();

		for (int i = 0; i < clefList.size(); i++) {

			if(clefList.get(i).getStaffNumber().equals(staff)){

				result = clefList.get(i);

			}

		}

		return result;

	}

	private void setCurrentNoteSet(Note note){

		boolean exists = false;

		for (int i = 0; i < currentNotes.size(); i++) {

			if(currentNotes.get(i).getVoice().getId().equals(note.getVoice().getId())){

				if(currentNotes.get(i).getStaff().equals(note.getStaff())){

					currentNotes.get(i).setSignature(note.getSignature());
					exists = true;

				}

			}

		}


		if(!exists)	{
			currentNotes.add(note);

		}

	}

	private Note getPreviousNoteSet(Note note){

		Note result = new Note();

		for (int i = 0; i < currentNotes.size(); i++) {

			if(currentNotes.get(i).getStaff()!=null){

				if(currentNotes.get(i).getStaff().equals(note.getStaff()) ){

					if(currentNotes.get(i).getVoice().getId().equals(note.getVoice().getId()) ){

						result = currentNotes.get(i);

					}


				}
			}
		}


		return result;
	}

	private String getCapital(String string){


		if(string != null && string != "" ) string = string.substring(0, 1).toUpperCase() + string.substring(1);

		return string;

	}

	public String getOutputFile(){

		return this.outputFile;

	}

	public void setOutputFile(String outputfile){

		this.outputFile = outputfile;
	}

	public void setInputFile(File musicxml){
		
		this.inputFile = musicxml;
		
	}
	
	public File getInputFile(){
		
		return this.inputFile;
		
	}
	
	public String getDocumentURI(){
		
		return this.documentURI;
	}

	public void setDocumentURI(String uri){
		
		this.documentURI = uri;
	}
	
	public void setDocumentTitle(String title){
		
		this.documentTitle = title;
		
	}
	
	public String getDocumentTitle(String title){
		
		return this.documentTitle;
		
	}
}