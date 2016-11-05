package com.example.web;

import com.example.dashboard.DashboardService;
import com.example.dashboard.ReactorPerson;
import com.example.dashboard.ReactorPersonNotFoundException;
import com.example.dashboard.ReactorPersonRepository;
import com.example.integration.gitter.GitterMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Brian Clozel
 */
@Controller
public class DashboardController {

	private final DashboardService dashboardService;

	private final ReactorPersonRepository repository;

	@Autowired
	public DashboardController(DashboardService dashboardService, ReactorPersonRepository repository) {
		this.dashboardService = dashboardService;
		this.repository = repository;
	}

	@GetMapping("/")
	public String home() {
		return "home";
	}

	@GetMapping("/issues")
	public Mono<String> issues(Model model) {

		return this.dashboardService.findReactorIssues()
				.collectList().then(list -> {
					model.addAttribute("issues", list);
					return Mono.just("issues");
				});
	}

	@GetMapping("/chat")
	public String chat(Model model) {
		return "chat";
	}

	@GetMapping(path = "/chatMessages", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public Flux<GitterMessage> chatMessages(@RequestParam(required = false, defaultValue = "10") String limit) {
		return this.dashboardService.getLatestChatMessages(Integer.parseInt(limit));
	}

	@GetMapping(value = "/chatStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseBody
	public Flux<GitterMessage> streamChatMessages() {
		return this.dashboardService.streamChatMessages();
	}

	@ExceptionHandler(ReactorPersonNotFoundException.class)
	public ResponseEntity handleNotFound() {
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/reactor/people/{id}")
	@ResponseBody
	public Mono<ReactorPerson> findReactorPeople(@PathVariable String id) {
		return this.repository.findOne(id)
				.otherwiseIfEmpty(Mono.error(new ReactorPersonNotFoundException(id)));
	}

}
