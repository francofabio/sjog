package br.com.binarti.sjog.model.events;

import java.util.Date;

import br.com.binarti.sjog.model.DocumentoEvent;
import br.com.binarti.sjog.model.EventType;

public class WordEvent extends DocumentoEvent {

	private String descriptionWordEvent;
	
	public WordEvent(int numberOfNewWords) {
		super(EventType.WORD, new Date(), new EventDetailWord(numberOfNewWords));
		this.descriptionWordEvent = "Word event";
	}
	
	@Override
	public EventDetailWord getEventDetail() {
		return (EventDetailWord) eventDetail;
	}
	
	public String getDescriptionWordEvent() {
		return descriptionWordEvent;
	}

}
