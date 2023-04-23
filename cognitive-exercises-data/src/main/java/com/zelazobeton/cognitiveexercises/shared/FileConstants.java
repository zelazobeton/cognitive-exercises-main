package com.zelazobeton.cognitiveexercises.shared;

public class FileConstants {
    private FileConstants(){}
    public static final String VERSION_1 = "/v1";
    public static final String MICROSERVICE_NAME = "/main";
    private static final String RESOURCES_FOLDER = "resources";
    public static final String FORWARD_SLASH = "/";
    public static final String USER_FOLDER = RESOURCES_FOLDER + "/users-data";
    public static final String GAMES_DATA_FOLDER = RESOURCES_FOLDER + "/games-data";

    public static final String DATA_GENERATION_FILES_FOLDER = RESOURCES_FOLDER + "/data-generation-files";
    public static final String EXAMPLE_USERNAMES_FILE = DATA_GENERATION_FILES_FOLDER + "/example_usernames.txt";
    public static final String DEFAULT_AVATAR_FILE = RESOURCES_FOLDER + "/defaults/default-avatar.png";

    public static final String AVATAR = "/avatar";
    public static final String DEFAULT_AVATAR_FILENAME = "avatar.jpg";
    public static final String PORTFOLIO_SERVICE = "/portfolio";
    public static final String NOT_AN_IMAGE_FILE = " is not an image file. Please upload an image file";
    public static final String DIRECTORY_CREATED = "Created directory for: ";
}
