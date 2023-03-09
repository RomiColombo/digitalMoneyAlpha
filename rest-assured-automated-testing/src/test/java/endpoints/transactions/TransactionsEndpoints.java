package endpoints.transactions;

public class TransactionsEndpoints {

    public static final String BASE_PATH = "/transactions";
    public static final String CREATE_TRANSACTION = BASE_PATH;
    public static final String GET_BY_ID = BASE_PATH +"/{id}";
    public static final String GET_BY_USER_ID_LIMIT = BASE_PATH +"/users/{userId}/{limit}";

    private TransactionsEndpoints() {}

}
