package constants;

public final class QueryTemplates {

    private QueryTemplates(){
        //default private constructor
    }

    public static final String BASE_SELECT_FROM_TEMPLATE = "SELECT * FROM {0}";
    public static final String BASE_SELECT_FROM_WHERE_TEMPLATE = "SELECT * FROM {0} WHERE {1}";
    public static final String BASE_SINGLE_SELECT_QUERY = "SELECT * FROM {0} LIMIT 1";
    public static final String BASE_SINGLE_SELECT_WHERE_QUERY = "SELECT * FROM {0} WHERE {1} LIMIT 1";
    public static final String WHERE_TEMPLATE = "WHERE {0}";
    public static final String CONDITION_TEMPLATE = "{0} = {1}";
    public static final String BASE_INSERT_TEMPLATE = "INSERT INTO {0} ({1}) VALUES ({2})";

}
