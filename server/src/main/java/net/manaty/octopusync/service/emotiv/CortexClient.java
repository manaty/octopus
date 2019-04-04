package net.manaty.octopusync.service.emotiv;

import io.reactivex.Completable;
import io.reactivex.Single;
import net.manaty.octopusync.service.emotiv.message.*;

import javax.annotation.Nullable;

public interface CortexClient {

    Completable connect();

    Single<GetUserLoginResponse> getUserLogin();

    Single<LoginResponse> login(String username, String password, String clientId, String clientSecret);

    Single<LogoutResponse> logout(String username);

    Single<AuthorizeResponse> authorize(String clientId, String clientSecret, @Nullable String license, int debit);

    Single<QuerySessionsResponse> querySessions(String authzToken);
}
