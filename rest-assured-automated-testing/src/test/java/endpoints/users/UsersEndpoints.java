package endpoints.users;

public class UsersEndpoints {

    private static final String BASE_URL = "/users";
    public static final String GET_BY_ID = BASE_URL + "/{id}";
    public static final String CREATE = BASE_URL;
    public static final String LOGIN = BASE_URL + "/login";
    public static final String LOGOUT = BASE_URL + "/logout";
    public static final String TOKEN_INTROSPECTION = BASE_URL + "realms/digitalMoneyHouse/protocol/openid-connect/token/introspect";
    public static final String TOKEN_URL = BASE_URL + "realms/digitalMoneyHouse/protocol/openid-connect/token";
    public static final String PATCH_UPDATE = BASE_URL + "/{id}";
    public static final String DELETE_BY_ID = BASE_URL +"/{id}";

    private UsersEndpoints() {}

}
