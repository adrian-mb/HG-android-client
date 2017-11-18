package hg.hg_android_client.login.repository;

public interface TokenRepository {

    String getToken();

    void updateToken(String token);

    void deleteToken();

}
