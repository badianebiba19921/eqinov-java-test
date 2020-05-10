package com.eqinov.test.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

public class MockApi {

	public static void main(String[] args) {
		WireMockServer wireMockServer = new WireMockServer(
				WireMockConfiguration.options().port(2345));
		wireMockServer.start();

		WireMock.configureFor(2345);

		WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/conso"))
				.willReturn(WireMock.aResponse()
						.withHeader("Content-Type", "application/json").withStatus(200).withBodyFile("conso.json")));
	}

}