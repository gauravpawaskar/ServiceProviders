package com.flagprotectors.auth0saml;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class Auth0samlApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Auth0samlApplication.class);
		application.run(args);
	}

	@EventListener(ApplicationStartedEvent.class)
	public void register() {

		String database_host = System.getenv("DATABASE_HOST");
		String database_port = System.getenv("DATABASE_PORT");
		String service_host = System.getenv("SERVICE_HOST");
		String service_port = System.getenv("SERVICE_PORT");

		if (database_host == null) {
			database_host = "0.0.0.0";
			database_port = "5000";
			service_host = "auth0saml-clusterip";
			service_port = "3003";
		}

		String database_url = "http://" + database_host + ":" + database_port + "/service";

		HttpClient httpClient = HttpClientBuilder.create().build();
		try {
			HttpPost request = new HttpPost(database_url);
			StringEntity params = new StringEntity(
					"{\"name\":\"auth0saml\",\"host\":\"" + service_host + "\", \"port\":\"" + service_port + "\"} ");
			request.addHeader("content-type", "application/json");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			Logger logger = LoggerFactory.getLogger(Auth0samlApplication.class);
			logger.info(response.getStatusLine().toString());

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// @Deprecated httpClient.getConnectionManager().shutdown();
		}

	}

}
