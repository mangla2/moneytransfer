package com.revolut.app.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;

import javax.ws.rs.HttpMethod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.revolut.app.constants.Constants;
import com.revolut.app.model.FixerError;
import com.revolut.app.model.FixerResponse;

public class CurrencyConverter {

	private static final String BASE_API = "http://data.fixer.io/api/latest";
	private static final String ACCESS_KEY = "65475af89d34cd232165f87cf25923f2";
	private static final String FORMAT_VALUE = "1";
	private static final Logger Logger = LogManager.getLogger(CurrencyConverter.class);

	public static BigDecimal getConversionRate(String fromCurrencyCode, String toCurrencyCode) {
		Logger.debug("Starting getConversionRate in CurrencyConverter from[{}] to[{}]", fromCurrencyCode, toCurrencyCode);
		BigDecimal conversionRate = new BigDecimal(0);

		if(StringUtils.isNullOrEmpty(fromCurrencyCode) || StringUtils.isNullOrEmpty(toCurrencyCode)){
			Logger.error("Fail to proceed further as Currency Code is null/empty");
			return conversionRate;
		}

		if (fromCurrencyCode.equals(toCurrencyCode)) {
			Logger.info("Same currency for both the accounts.Hence no need to check conversion rate");
			return conversionRate.ONE;
		}

		Logger.info("Checking the latest conversion rate from {} to {}", fromCurrencyCode, toCurrencyCode);
		try{
			StringBuilder url = new StringBuilder(BASE_API);
			url.append(Constants.ACCESS_KEY_PARAM)
			.append(ACCESS_KEY)
			.append(Constants.FORMAT_KEY_PARAM)
			.append(FORMAT_VALUE);

			FixerResponse response = getConversionResponse(url.toString());

			if (response != null) {
				Object rates = response.getRates();
				Gson gson = new Gson();
				LinkedHashMap<String,Double> ratesMap = gson.fromJson(rates.toString(), LinkedHashMap.class);
				Double from = ratesMap.get(fromCurrencyCode);
				Double to = ratesMap.get(toCurrencyCode);

				if(from == null || to == null){
					Logger.error("Conversion Currency specified is incorrect or cannot be found for conversion");
					return conversionRate;
				}

				double conversionResult = ("EUR".equalsIgnoreCase(fromCurrencyCode)) ? from*to : from / to;
				conversionRate = new BigDecimal(conversionResult);
				conversionRate = conversionRate.setScale(2, BigDecimal.ROUND_HALF_EVEN);
			}
		}catch(Exception e){
			Logger.error("Failed to get the conversion rate from third party fixer api - {}", e.getMessage());
		}
		return conversionRate;
	}

	private static FixerResponse getConversionResponse(String baseUrl){
		Logger.debug("Starting getConversionResponse in CurrencyConverter calling API-{}", baseUrl);
		FixerResponse resp = new FixerResponse();
		BufferedReader reader = null;
		HttpURLConnection connection = null;
		URL url = null;
		try {
			Logger.info("Calling API to check the latest conversion rate {}", baseUrl);
			url = new URL(baseUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(HttpMethod.GET);

			if (connection.getResponseCode() == 200) {
				InputStream inputStream = connection.getInputStream();
				InputStreamReader ipr = new InputStreamReader(inputStream);
				reader = new BufferedReader(ipr);
				String inputLine;
				StringBuffer jsonString = new StringBuffer();
				while ((inputLine = reader.readLine()) != null) {
					jsonString.append(inputLine);
				}
				resp = new ObjectMapper().readValue(jsonString.toString(), FixerResponse.class);
				Logger.info("Response received for conversion rates {}", resp);
			}else{
				resp = new FixerResponse(false,new FixerError(Constants.ERROR_CODE_PROCESSING,"Could not connect to Fixer API successfully"));
			}
		}catch(Exception e){
			Logger.error("Exception occured while getting the currency conversion response - {}", e.getMessage());
			resp = new FixerResponse(false,new FixerError(Constants.ERROR_CODE_EXCEPTION,"Exception occured while getting the currency conversion response"+ e.getLocalizedMessage()));
		}
		return resp;
	}
}
