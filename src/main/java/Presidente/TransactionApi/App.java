package Presidente.TransactionApi;
import Presidente.TransactionApi.DbFunctions;

public class App 
{
    public static void main( String[] args )
    {
        DbFunctions db = new DbFunctions(); 
        db.connect();
    }
}
