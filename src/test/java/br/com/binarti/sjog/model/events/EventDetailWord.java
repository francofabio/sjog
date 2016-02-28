package br.com.binarti.sjog.model.events;

import br.com.binarti.sjog.model.EventDetail;

public class EventDetailWord extends EventDetail {

	private int numberOfNewWords;

	public EventDetailWord(int numberOfNewWords) {
		this.numberOfNewWords = numberOfNewWords;
	}
	
	public int getNumberOfNewWords() {
		return numberOfNewWords;
	}

	public void setNumberOfNewWords(int numberOfNewWords) {
		this.numberOfNewWords = numberOfNewWords;
	}
	
}
