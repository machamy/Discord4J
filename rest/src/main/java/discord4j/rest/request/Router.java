package discord4j.rest.request;

import discord4j.rest.http.client.SimpleHttpClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Router {

	private final SimpleHttpClient httpClient;
	private final Map<Bucket, RequestStream<?>> streamMap = new ConcurrentHashMap<>();

	public Router(SimpleHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public <T> Mono<T> exchange(DiscordRequest<T> request) {
		return Mono.defer(() -> {
			RequestStream<T> stream = getStream(request);
			stream.push(request);
			return request.mono();
		}).cache();
	}

	@SuppressWarnings("unchecked")
	private <T> RequestStream<T> getStream(DiscordRequest<T> request) {
		return (RequestStream<T>) streamMap.computeIfAbsent(request.getBucket(), k -> {
			RequestStream<T> stream = new RequestStream<>();

			stream.read().subscribe(new Consumer<DiscordRequest<T>>() {
				@SuppressWarnings("ConstantConditions")
				@Override
				public void accept(DiscordRequest<T> req) {
					httpClient.exchange(req.getMethod(), req.getUri(), req.getBody(), req.getResponseType())
							.materialize()
							.subscribe(signal -> {
								if (signal.isOnSubscribe()) {
									req.mono.onSubscribe(signal.getSubscription());
								} else if (signal.isOnNext()) {
									req.mono.onNext(signal.get());
								} else if (signal.isOnError()) {
									req.mono.onError(signal.getThrowable());
								} else if (signal.isOnComplete()) {
									req.mono.onComplete();
								}
								stream.read().subscribe(this);
							});
				}
			});
			return stream;
		});
	}
}