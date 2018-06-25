package com.example.algamoney.api.exceptionHandler;

public class Erro {
	
	private String msgView;
	private String msgStackTrace;
	
	public Erro(String msgView, String msgStackTrace) {
		this.msgView = msgView;
		this.msgStackTrace = msgStackTrace;
	}

	public String getMsgView() {
		return msgView;
	}

	public String getMsgStackTrace() {
		return msgStackTrace;
	}
	
}
