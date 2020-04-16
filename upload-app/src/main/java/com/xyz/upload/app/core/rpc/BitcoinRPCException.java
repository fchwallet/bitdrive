package com.xyz.upload.app.core.rpc;

import wf.bitcoin.javabitcoindrpcclient.BitcoinRpcException;

public class BitcoinRPCException extends BitcoinRpcException {

	  private String rpcMethod;
	  private String rpcParams;
	  private int responseCode;
	  private String responseMessage;
	  private String response;

	  /**
	   * Creates a new instance of <code>BitcoinRPCException</code> with response
	   * detail.
	   *
	   * @param method the rpc method called
	   * @param params the parameters sent
	   * @param responseCode the HTTP code received
	   * @param responseMessage the HTTP response message
	   * @param response the error stream received
	   */
	  public BitcoinRPCException(String method, String params, int responseCode, String responseMessage, String response) {
	    super("RPC Query Failed (method: " + method + ", params: " + params + ", response code: " + responseCode + " responseMessage " + responseMessage + ", response: " + response);
	    this.rpcMethod = method;
	    this.rpcParams = params;
	    this.responseCode = responseCode;
	    this.responseMessage = responseMessage;
	    this.response = response;
	  }

	  public BitcoinRPCException(String method, String params, Throwable cause) {
	    super("RPC Query Failed (method: " + method + ", params: " + params + ")", cause);
	    this.rpcMethod = method;
	    this.rpcParams = params;
	  }

	  /**
	   * Constructs an instance of <code>BitcoinRPCException</code> with the
	   * specified detail message.
	   *
	   * @param msg the detail message.
	   */
	  public BitcoinRPCException(String msg) {
	    super(msg);
	  }

	  public BitcoinRPCException(String message, Throwable cause) {
	    super(message, cause);
	  }

	  public int getResponseCode() {
	    return responseCode;
	  }

	  public String getRpcMethod() {
	    return rpcMethod;
	  }

	  public String getRpcParams() {
	    return rpcParams;
	  }

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	}

