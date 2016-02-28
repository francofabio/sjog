package br.com.binarti.sjog.model;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;

public abstract class DocumentoEvent {

	protected EventType type;
	protected Date date;
	protected EventDetail eventDetail;

	protected DocumentoEvent(EventType type, Date date, EventDetail eventDetail) {
		this.type = type;
		this.date = date;
		this.eventDetail = eventDetail;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public abstract EventDetail getEventDetail();

	public static void main(String[] args) throws Exception {
		Method[] methods = DocumentoEvent.class.getMethods();
		for (Method method : methods) {
			System.out.println(method);
			System.out.println("isAbstract: " + Modifier.isAbstract(method.getModifiers()));
			System.out.println("----------------------");
		}
	}
	
}
