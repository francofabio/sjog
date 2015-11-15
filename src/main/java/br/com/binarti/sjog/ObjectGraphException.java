package br.com.binarti.sjog;

@SuppressWarnings("serial")
public class ObjectGraphException extends RuntimeException {

	public ObjectGraphException(String message) {
		super(message);
	}

	public ObjectGraphException(String message, Throwable cause) {
		super(message, cause);
	}

}
