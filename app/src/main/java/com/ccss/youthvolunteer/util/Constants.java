package com.ccss.youthvolunteer.util;

public class Constants {

    public static final String CCSS_URL = "http://www.carecom.org.sg/";
    public static final String CCSS_FACEBOOK_PAGE = "https://www.facebook.com/carecommunityservicessociety";

    public static final String PARSE_APPLICATION_KEY = "zgf2GHFb5OlcmPHhnqcByNdGickfavaWqopQhvGw";
    public static final String PARSE_CLIENT_KEY = "4enb4bsmZrZywUjOeVCQWvyPxrCdKHQw8d9u03Sb";

    public static final String FB_APP_ID = "985731814799032";
    public static final String GOOGLE_API_KEY = "AIzaSyCewRzoZ23-pJFH-oYcYD2tyXo9eXqWU9M";

    public static final String POST_BROADCAST_CHANNEL = "broadcastPost";
    public static final String OPPORTUNITY_BROADCAST_CHANNEL = "broadcastOpportunity";
    public static final String PLACE_ID = "place_id";

    public static final String GOOGLE_PLACE_API_URL = "https://maps.googleapis.com/maps/api/place/";

    public static final String LOCATION_LAT = "location_lat";
    public static final String LOCATION_LNG = "location_lng";
    public static final String LOCATION_ADDRESS = "location_address";
    public static final String LAST_KNOWN_LOCATION = "lastKnownLocation";

    public static final int MINIMUM_AGE = 12;
    public static final int MAXIMUM_AGE = 45;

    public static final String INTENT_SENDER = "Sender";
    public static final String INTERNET_AVAILABLE = "Connectivity";

    public static final String ADMIN_ROLE = "Admin";
    public static final String MODERATOR_ROLE = "Moderator";
    public static final String ORGANIZER_ROLE = "Organizer";
    public static final String SUPERVISOR_ROLE = "Supervisor";

    public static final String MANAGE_ITEM_KEY = "ManageItemKey";
    public static final String CATEGORY_RESOURCE = "Category";
    public static final String ANNOUNCEMENT_RESOURCE = "Announcement";
    public static final String RECOGNITION_RESOURCE = "Recognition";
    public static final String INTEREST_RESOURCE = "Interest";
    public static final String SKILL_RESOURCE = "Skill";
    public static final String USER_RESOURCE = "SpecialUser";
    public static final String SCHOOL_RESOURCE = "School";
    public static final String GROUP_RESOURCE = "Group";
    public static final String ORGANIZATION_RESOURCE = "Organization";
    public static final String OPPORTUNITY_RESOURCE = "Opportunity";
    public static final String USER_ORGANIZATION_KEY = "UserOrganization";
    public static final String VOLUNTEER_USER_RESOURCE = "VolunteerUser";
    public static final String USER_ACTION_RESOURCE = "UserAction";
    public static final String USER_RECOGNITION_RESOURCE = "UserRecognition";

    public static final String ERROR_ITEM_KEY = "ErrorItemKey";
    public static final String OBJECT_ID_KEY = "objectId";

    public static final int COOLING_PERIOD_BETWEEN_ACTIONS_IN_MINS = 10;

    public static final int VOLUNTEER_ACTIVITY_REQUEST_CODE = 22771;
    public static final int IMAGE_PICKER_REQUEST_CODE = 22772;
    public static final int ADD_RESOURCE_REQUEST_CODE = 22773;
    public static final int MANAGE_RESOURCE_REQUEST_CODE = 22774;
    public static final int PLACE_PICKER_REQUEST_CODE = 22775;

    public static final String ENCOURAGE_KEY = "ENCOURAGE";
    public static final String GOODNESS_KEY = "GOODNESS";
    public static final String POINTS_RANK_KEY = "OWNPOINTS";
    public static final String SG_STATS_KEY = "SGSTATS";
    public static final String UPCOMING_KEY = "UPCOMING";
    public static final String ANNOUNCEMENTS_KEY = "ANNOUNCEMENTS";
    public static final String USER_STATS_KEY = "USERSTATS";
    public static final String USER_CURRENT_MONTH_STATS_KEY = "USERCURRENTMONTHSTATS";
    public static final String STATS_LAST_UPDATE_DATE = "STATSLASTUPDATED";
    public static final String CURRENT_USER_POINTS = "CurrentUserPoints";


    public static final String PREF_FILE_NAME = "YouthVolunteer";

    //Enter in lowercase only
    public static final String ACCESS_MODE_KEY = "mode";
    public static final String READ_MODE = "read";
    public static final String WRITE_MODE = "edit";
    public static final String EDIT_MODE = "edit";
    public static final String ADD_MODE = "add";

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String MONTH_YEAR_FORMAT = "MMM-yyyy";
    public static final int PROFILE_IMAGE_SIZE = 100;
    public static final int LIST_IMAGE_SIZE = 50;
    public static final float INACTIVE_ALPHA = 0.5f;
    public static final String APPROVE = "Approve";
    public static final String PENDING = "Pending";
    public static final String REJECT = "Reject";
    public static final String VIRTUAL = "Virtual";

    public enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }
}