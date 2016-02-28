package br.com.binarti.sjog.model.events;

import br.com.binarti.sjog.model.EventDetail;

public class EventDetailTable extends EventDetail {

	private int numberOfNewCells;

	public EventDetailTable(int numberOfNewCells) {
		this.setNumberOfNewCells(numberOfNewCells);
	}

	public int getNumberOfNewCells() {
		return numberOfNewCells;
	}

	public void setNumberOfNewCells(int numberOfNewCells) {
		this.numberOfNewCells = numberOfNewCells;
	}
	
}
