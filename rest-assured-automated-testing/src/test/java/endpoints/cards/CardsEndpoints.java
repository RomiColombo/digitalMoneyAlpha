package endpoints.cards;


public class CardsEndpoints {

    public static final String BASE_PATH = "/cards";

    public static final String SAVE_CARD = BASE_PATH + "/";

    public static final String GET_BY_ACCOUNT_ID = BASE_PATH +"/getAll/{accountId}";

    public static final String DELETE_BY_ID = BASE_PATH + "/{id}";

    public static final String FIND_BY_ID = BASE_PATH +"/getCard/{cardId}";


    /**
     * Constructor of the class.
     */
    private CardsEndpoints() {
    }

}
