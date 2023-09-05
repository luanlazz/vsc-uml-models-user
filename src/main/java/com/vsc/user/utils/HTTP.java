package com.vsc.user.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HTTP {

	public static HTTPResponse sendRequest(String url, Map<String, String> parameters) throws Exception {
		URL obj = new URL(url);

		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		String paramsString = buildParams(parameters);

		try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
			byte[] requestBodyBytes = paramsString.getBytes(StandardCharsets.UTF_8);
			outputStream.write(requestBodyBytes);
			outputStream.flush();
		}
				
		int responseCode = connection.getResponseCode();
		
		try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		}
		
		return new HTTPResponse(responseCode, null, null);				
	}
	
	private static String buildParams(Map<String, String> parameters) {
		StringBuilder paramsBuilder = new StringBuilder();
		
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			if (paramsBuilder.length() > 0) {
				paramsBuilder.append("&");
			}
			paramsBuilder.append(entry.getKey()).append("=").append(entry.getValue());
		}
		
		return paramsBuilder.toString();
	}
}
