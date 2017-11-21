package tcn.com.handle;

//All APIs
public class Constants {

    //Type
    public static final int LEARN = 0;
    public static final int LEARNED = 16;
    public static final int DELETE_NOTE = 1;
    public static final int INSERT_VOCABULARY = 2;
    public static final int EDIT_VOCABULARY = 3;
    public static final int GET_VOCABULARY = 4;
    public static final int BACKUP_VOCABULARY = 5;
    public static final int BACKUP_TOPIC = 6;
    public static final int DELETE_TOPIC = 7;
    public static final int GET_NAME_TOPIC = 8;
    public static final int GET_TOPIC = 9;
    public static final int GET_TOPIC_FRIENDS = 10;
    public static final int GET_ALL_TOPIC = 11;
    public static final int LOGIN = 12;
    public static final int GET_INFO_USER = 13;
    public static final int SHARE = 14;
    public static final int INVITE = 15;


    /// api for http://bdata.dlinkddns.com

    //APP URL

    public static final String GET_LINK_APP_URL = "http://bdata.dlinkddns.com/englishbigger/api/appurl/";

    //Insert topic
    public static final String INSERT_URL = "http://bdata.dlinkddns.com/englishbigger/api/topic/insert.php";

    //Get all of that user's topic
    public static final String TOPIC_URL = "http://bdata.dlinkddns.com/englishbigger/api/topic/";

    //Get all topic
    public static final String ALL_TOPIC_URL = "http://bdata.dlinkddns.com/englishbigger/api/topic/all.php";

    //Get topic of fiends
    public static final String TOPIC_OF_FRIENDS_URL = "http://bdata.dlinkddns.com/englishbigger/api/topic/friends.php";

    //Backup topic
    public static final String BACKUP_TOPIC_URL = "http://bdata.dlinkddns.com/englishbigger/api/topic/backup.php";

    //Backup vocabulary
    public static final String BACKUP_VOCABULARY_URL = "http://bdata.dlinkddns.com/englishbigger/api/vocabulary/backup.php";

    //Update topic
    public static final String UPDATE_URL = "http://bdata.dlinkddns.com/englishbigger/api/topic/update.php";

    //Delete topic
    public static final String DELETE_URL = "http://bdata.dlinkddns.com/englishbigger/api/topic/delete.php";

    //Used to register account
    public static final String USER_URL = "http://bdata.dlinkddns.com/englishbigger/api/users/";

    //Used to get account information
    public static final String USER_INFO_URL = "http://bdata.dlinkddns.com/englishbigger/api/usersinfo/";

    //Used to add, edit, delete, or retrieve all vocabulary in each topic
    public static final String VOCABULARY_URL = "http://bdata.dlinkddns.com/englishbigger/api/vocabulary/";

    //Use to update from learned
    public static final String LEARNED_URL = "http://bdata.dlinkddns.com/englishbigger/api/vocabulary/learned.php";


    /// api for http://mybigger.ga

//    //APP URL
//
//    public static final String GET_LINK_APP_URL = "http://mybigger.ga/api/appurl/";
//
//    //Insert topic
//    public static final String INSERT_URL = "http://mybigger.ga/api/topic/insert.php";
//
//    //Get all of that user's topic
//    public static final String TOPIC_URL = "http://mybigger.ga/api/topic/";
//
//    //Update topic
//    public static final String UPDATE_URL = "http://mybigger.ga/api/topic/update.php";
//
//    //Delete topic
//    public static final String DELETE_URL = "http://mybigger.ga/api/topic/delete.php";
//
//    //Get all topic
//    public static final String ALL_TOPIC_URL = "http://mybigger.ga/api/topic/all.php";
//
//    //Get topic of fiends
//    public static final String TOPIC_OF_FRIENDS_URL = "http://mybigger.ga/api/topic/friends.php";
//
//    //Backup topic
//    public static final String BACKUP_TOPIC_URL = "http://mybigger.ga/api/topic/backup.php";
//
//    //Backup vocabulary
//    public static final String BACKUP_VOCABULARY_URL = "http://mybigger.ga/api/vocabulary/backup.php";
//
//    //Used to register account
//    public static final String USER_URL = "http://mybigger.ga/api/users/";
//
//    //Used to get account information
//    public static final String USER_INFO_URL = "http://mybigger.ga/api/usersinfo/";
//
//    //Used to add, edit, delete, or retrieve all vocabulary in each topic
//    public static final String VOCABULARY_URL = "http://mybigger.ga/api/vocabulary/";
//
//    //Use to update from learned
//    public static final String LEARNED_URL = "http://mybigger.ga/api/vocabulary/learned.php";
}
