package endpoints.accounts;

public class AccountsEndpoints {

    public static final String BASE_PATH = "/accounts";
    public static final String GET_BY_ID = BASE_PATH +"/{id}";
    public static final String GET_BY_USER_ID = BASE_PATH +"/user/{userId}";
    public static final String GET_BY_ALIAS = BASE_PATH +"/alias/{alias}";
    public static final String CREATE_ACCOUNT = BASE_PATH;

    public static final String UPDATE_ALIAS = BASE_PATH +"/{id}";


    private AccountsEndpoints() {}

}
