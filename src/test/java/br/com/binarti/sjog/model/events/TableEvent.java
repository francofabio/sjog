package br.com.binarti.sjog.model.events;

import java.util.Date;

import br.com.binarti.sjog.model.DocumentoEvent;
import br.com.binarti.sjog.model.EventType;

public class TableEvent extends DocumentoEvent {

	private String descriptionTableEvent;
	
	public TableEvent(int numberOfNewCells) {
		super(EventType.TABLE, new Date(), new EventDetailTable(numberOfNewCells));
		this.descriptionTableEvent = "Table event";
	}
	
	@Override
	public EventDetailTable getEventDetail() {
		return (EventDetailTable) eventDetail;
	}
	
	public String getDescriptionTableEvent() {
		return descriptionTableEvent;
	}

}
