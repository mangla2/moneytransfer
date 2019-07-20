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
import com.revolut.app.model.AppResponse;
import com.revolut.app.model.ErrorDetails;
import com.revolut.app.model.FixerError;
import com.revolut.app.model.FixerResponse;

public class CurrencyConverter {

	private static final String BASE_API = "http://data.fixer.io/api/latest";
	private static final String ACCESS_KEY = "65475af89d34cd232165f87cf25923f2";
	private static final String FORMAT_VALUE = "1";
	private static final Logger Logger = LogManager.getLogger(CurrencyConverter.class);

	public static AppResponse getConversionRate(String fromCurrencyCode, String toCurrencyCode) {
		Logger.debug("Starting getConversionRate in CurrencyConverter from[{}] to[{}]", fromCurrencyCode, toCurrencyCode);
		BigDecimal conversionRate = new BigDecimal(0);

		if(StringUtils.isNullOrEmpty(fromCurrencyCode) || StringUtils.isNullOrEmpty(toCurrencyCode)){
			Logger.error("Fail to proceed further as Currency Code is null/empty");
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_VALIDATION, "Fail to proceed further as Currency Code is null/empty"));
		}

		if (fromCurrencyCode.equals(toCurrencyCode)) {
			Logger.info("Same currency for both the accounts.Hence no need to check conversion rate");
			return new AppResponse(true, conversionRate.ONE);
		}

		Logger.info("Checking the latest conversion rate from {} to {}", fromCurrencyCode, toCurrencyCode);
		try{
			StringBuilder url = new StringBuilder(BASE_API);
			url.append(Constants.ACCESS_KEY_PARAM)
			.append(ACCESS_KEY)
			.append(Constants.FORMAT_KEY_PARAM)
			.append(FORMAT_VALUE);

			FixerResponse response = getConversionResponse(url.toString());

			if (!response.getSuccess()) {
				Logger.error(response.getError().getType());
				return new AppResponse(false, new ErrorDetails(response.getError().getCode(), response.getError().getType()));
			}

			Object rates = response.getRates();
			Gson gson = new Gson();
			LinkedHashMap<String,Double> ratesMap = gson.fromJson(rates.toString(), LinkedHashMap.class);
			Double from = ratesMap.get(fromCurrencyCode);
			Double to = ratesMap.get(toCurrencyCode);

			if(from == null || to == null){
				Logger.error("Conversion Currency specified is incorrect or cannot be found for conversion");
				return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_PROCESSING, "Fail to proceed further as Currency Code cannot be found"));
			}

			double conversionResult = ("EUR".equalsIgnoreCase(fromCurrencyCode)) ? from*to : from / to;

			conversionRate = new BigDecimal(conversionResult);
			conversionRate = conversionRate.setScale(2, BigDecimal.ROUND_HALF_EVEN);
			Logger.info("Conversion rate found to be {}", conversionRate);

		}catch(Exception e){
			Logger.error("Failed to get the conversion rate from third party fixer api - {}", e.getMessage());
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION, "Exception occured :"+ e.getMessage()));
		}
		return new AppResponse(true, conversionRate);
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
			resp = new FixerResponse(false,new FixerError(Constants.ERROR_CODE_EXCEPTION," Not able to connect to API while the currency conversion response "+ e.getMessage()));
		}
		return resp;
	}

	public static AppResponse checkValidCurrency(String currCode) {

		if(StringUtils.isNullOrEmpty(currCode)){
			Logger.error("Fail to proceed further as Currency Code received is null/empty");
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_VALIDATION, "Currency Code received is null/empty"));
		}

		Logger.info("Checking the validity of currency {}", currCode);
		try{
			StringBuilder url = new StringBuilder(BASE_API);
			url.append(Constants.ACCESS_KEY_PARAM)
			.append(ACCESS_KEY)
			.append(Constants.FORMAT_KEY_PARAM)
			.append(FORMAT_VALUE);

			FixerResponse response = getConversionResponse(url.toString());

			if (!response.getSuccess()) {
				Logger.error(response.getError().getType());
				return new AppResponse(false, new ErrorDetails(response.getError().getCode(), response.getError().getType()));
			}

			Object rates = response.getRates();
			Gson gson = new Gson();
			LinkedHashMap<String,Double> ratesMap = gson.fromJson(rates.toString(), LinkedHashMap.class);

			if(!ratesMap.containsKey(currCode.toUpperCase())){
				Logger.error("Requested currency not supported");
				return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_VALIDATION, "Requested currency not supported"));
			}

			return new AppResponse(true,true);
		}catch(Exception e){
			Logger.error("Exception occured while getting the currency conversion response - {}", e.getMessage());
			return new AppResponse(false,new ErrorDetails(Constants.ERROR_CODE_EXCEPTION," Not able to connect to API while the currency conversion response "+ e.getMessage()));
		}
	}
}
