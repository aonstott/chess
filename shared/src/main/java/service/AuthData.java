package service;
import java.util.Objects;
import java.util.UUID;


public class AuthData {
    private final String authorization;

    public AuthData()
    {
        this.authorization = UUID.randomUUID().toString();

    }

    public AuthData(String authorization)
    {
        this.authorization = authorization;
    }

    public String getAuthToken() {
        return authorization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthData authData = (AuthData) o;
        return Objects.equals(authorization, authData.authorization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorization);
    }

    @Override
    public String toString() {
        return authorization;
    }
}
