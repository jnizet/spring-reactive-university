package com.example.integration.github;

import com.example.DashboardProperties;
import reactor.core.publisher.Flux;

import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.client.reactive.ClientRequest;
import org.springframework.web.client.reactive.ExchangeFilterFunction;
import org.springframework.web.client.reactive.ExchangeFilterFunctions;
import org.springframework.web.client.reactive.WebClient;

/**
 * @author Brian Clozel
 */
@Component
public class GithubClient {

	private static final MediaType VND_GITHUB_V3 = MediaType.valueOf("application/vnd.github.v3+json");

	private final WebClient webClient;

	public GithubClient(DashboardProperties properties) {
		this.webClient = WebClient
				.builder(new ReactorClientHttpConnector())
				.filter(ExchangeFilterFunctions
						.basicAuthentication(properties.getGithub().getUsername(),
								properties.getGithub().getToken()))
				.filter(userAgent())
				.build();
	}

	public Flux<GithubIssue> findOpenIssues(String owner, String repo) {
		ClientRequest request = ClientRequest
				.GET("https://api.github.com/repos/{owner}/{repo}/issues?state=open", owner, repo)
				.accept(VND_GITHUB_V3)
				.build();

		return this.webClient.retrieveFlux(request, GithubIssue.class);
	}


	private ExchangeFilterFunction userAgent() {
		return (clientRequest, exchangeFunction) -> {
			ClientRequest newRequest = ClientRequest
					.from(clientRequest)
					.header("User-Agent", "Spring Framework WebClient")
					.build();
			return exchangeFunction.exchange(newRequest);
		};
	}
}
