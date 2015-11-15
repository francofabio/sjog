package br.com.binarti.sjog.model;

public class WrapperData<T> {

	private T data;

	public WrapperData(T data) {
		this.data = data;
	}
	
	public T getData() {
		return data;
	}
	
}
