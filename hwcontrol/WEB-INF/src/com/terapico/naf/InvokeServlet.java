package com.terapico.naf;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.challenge.cube.CubeClient;

import hwcontrol.ExecutionResult;
import hwcontrol.HomeControl;
import hwcontrol.HomeControlAPI;

public class InvokeServlet extends HttpServlet {
	
	
	
	
	InvokeHelper helper;
	@Override
	public void init() throws ServletException {
		try {
			helper=new InvokeHelper();
		} catch (UnknownHostException exception) {
			// TODO Auto-generated catch block
			exception.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient Logger log = Logger.getLogger(InvokeServlet.class.getName());
	/*
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CubeClient test = CubeClient.instance();
		
		logInfo(request.getRequestURI());
		BaseInvokeResult result =helper.getResult(test,request, response);
		response.setCharacterEncoding("GBK");		
		response.setContentType("text/html; encoding=UTF-8");
		response.addHeader("Cache-Control", "no-cache, must-revalidate");
		
		
		Object actualResult= result.getActualResult();
		
		if(actualResult instanceof ExecutionResult){
			
			ExecutionResult executionResult=(ExecutionResult)actualResult;
			executionResult.setWebCommand(request.getRequestURI());
			response.addHeader("X-GW-Error-Code", executionResult.getErrorCode()+"");
			response.addHeader("X-GW-Response", executionResult.getReceivedResponse());
		}
		
		request.setAttribute("result", result.getActualResult());
		this.dispatchView(request, response,result);
	}

	protected void dispatchView(HttpServletRequest request, HttpServletResponse response,BaseInvokeResult result) throws ServletException, IOException {
		RequestDispatcher view = getRenderView(request, result);
		view. include(request, response);

	}
	protected void logInfo(String message)
	{
		log.log(Level.INFO, message);
		
	}
	protected RequestDispatcher getRenderView(HttpServletRequest request, BaseInvokeResult result) throws MalformedURLException
	{
		if(!result.isGenericResult()){
			return getSimpleRenderView(request,result.getActualResult());
		}
		
		return request.getRequestDispatcher("/"+result.getRenderKey()+".jsp");
		
	}
	protected RequestDispatcher getSimpleRenderView(HttpServletRequest request, Object object) throws MalformedURLException {

		
		if(object==null){
			return request.getRequestDispatcher("/messageOK.jsp");
		}
		
		Class temp = object.getClass();
		while (temp != null) {
			String jsp = "/" + temp.getName() + ".jsp";
			logInfo("trying to find: "+jsp);
			URL url = getServletContext().getResource(jsp);
			if (url != null) {
				return request.getRequestDispatcher(jsp);
			}
			temp = temp.getSuperclass();
		}
		return request.getRequestDispatcher("/java.lang.Object.jsp");// should
																		// not
																		// go
																		// here

	}
	


}
