package br.com.binarti.sjog.model;

import java.util.List;

public class Page<T> {

	private int page;
	private int total;
	private List<T> content;

	public Page(int page, int total, List<T> content) {
		this.page = page;
		this.total = total;
		this.content = content;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

}
