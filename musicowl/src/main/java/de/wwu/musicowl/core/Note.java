package de.wwu.musicowl.core;

import java.util.ArrayList;

public class Note {

	private String octave;
	private String pitch;
	private String voice;
	private String type;
	private String staff;
	private String accidental;
	private Clef clef;
	private String slur;
	private ArrayList<String> dynamics;
	private ArrayList<String> articulations;
	private boolean isDot;
	private boolean isChord;
	
	private String signature;
	
	public Note() {

		super();
		this.dynamics = new ArrayList<String>();
		this.articulations = new ArrayList<String>();
		this.clef = new Clef();
		this.accidental = "";
		isChord = false;
		isDot = false;

	}

	public String getOctave() {
		return octave;
	}

	public void setOctave(String octave) {
		this.octave = octave;
	}

	public String getPitch() {
		return pitch;
	}

	public void setPitch(String pitch) {
		this.pitch = pitch;
	}

	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isDot() {
		return isDot;
	}

	public void setDot(boolean isDot) {
		this.isDot = isDot;
	}

	public boolean isChord() {
		return isChord;
	}

	public void setChord(boolean isChord) {
		this.isChord = isChord;
	}

	public String getStaff() {
		return staff;
	}

	public void setStaff(String staff) {
		this.staff = staff;
	}

	public String getAccidental() {
		return accidental;
	}

	public void setAccidental(String accidental) {
		this.accidental = accidental;
	}

	public ArrayList<String> getDynamics() {
		return dynamics;
	}

	
	public ArrayList<String> getArticulations() {
		return articulations;
	}

	public Clef getClef() {
		return clef;
	}

	public String getSlur() {
		return slur;
	}

	public void setSlur(String slur) {
		this.slur = slur;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	
	
	
}
