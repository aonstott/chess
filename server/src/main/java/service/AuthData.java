package service;
import java.util.Objects;
import java.util.UUID;


public class AuthData {
    private final String authToken;

    public AuthData()
    {
        this.authToken = UUID.randomUUID().toString();

    }

    public String getAuthToken() {
        return authToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthData authData = (AuthData) o;
        return Objects.equals(authToken, authData.authToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken);
    }

    @Override
    public String toString() {
        return "AuthData{" +
                "authToken='" + authToken + '\'' +
                '}';
    }
}
