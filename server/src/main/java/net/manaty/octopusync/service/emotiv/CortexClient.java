package net.manaty.octopusync.service.emotiv;

import io.reactivex.Completable;
import io.reactivex.Single;
import net.manaty.octopusync.service.emotiv.event.CortexEvent;
import net.manaty.octopusync.service.emotiv.event.CortexEventKind;
import net.manaty.octopusync.service.emotiv.message.*;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Consumer;

public interface CortexClient {

    Completable connect();

    Single<GetUserLoginResponse> getUserLogin();

    Single<LoginResponse> login(String username, String password, String clientId, String clientSecret);

    Single<LogoutResponse> logout(String username);

    Single<AuthorizeResponse> authorize(String clientId, String clientSecret, @Nullable String license, int debit);

    Single<QueryHeadsetsResponse> queryHeadsets();

    Single<QuerySessionsResponse> querySessions(String authzToken, String appId);

    Single<CreateSessionResponse> createSession(String authzToken, String headset, Session.Status status);

    Single<UpdateSessionResponse> updateSession(String authzToken, String session, Session.Status status);

    Single<SubscribeResponse> subscribe(String authzToken, Set<CortexEventKind> streams, String sessionId, Consumer<CortexEvent> eventListener);
}
