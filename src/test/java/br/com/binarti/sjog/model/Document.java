package br.com.binarti.sjog.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Document {

	protected String title;
	protected Date creationDate;
	
	private List<DocumentoEvent> events;
	
	public Document(String title) {
		this.title = title;
		this.creationDate = new Date();
		this.events = new ArrayList<>();
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public List<DocumentoEvent> getEvents() {
		return events;
	}

	public void setEvents(List<DocumentoEvent> events) {
		this.events = events;
	}
	
	public <T extends DocumentoEvent> T addEvent(T event) {
		this.events.add(event);
		return event;
	}
	
}
