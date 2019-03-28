package net.manaty.octopusync.service.emotiv;

import io.reactivex.Completable;
import io.reactivex.Single;
import net.manaty.octopusync.service.emotiv.message.LoginResponse;

public interface CortexClient {

    Completable connect();

    Single<LoginResponse> login(String username, String password, String clientId, String clientSecret);
}
