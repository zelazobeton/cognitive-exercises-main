package com.zelazobeton.cognitiveexercieses.constant;

public class FileConstants {
    private FileConstants(){}
    public static final String LOCALHOST_ADDRESS = "http://localhost:8081";
    private static final String RESOURCES_FOLDER = "resources";
    public static final String FORWARD_SLASH = "/";
    public static final String USER_FOLDER = RESOURCES_FOLDER + "/users-data";
    public static final String GAMES_DATA_FOLDER = RESOURCES_FOLDER + "/games-data";
    public static final String MEMORY_IMG_FOLDER = RESOURCES_FOLDER + "/memory-pictures";

    public static final String DATA_GENERATION_FILES_FOLDER = RESOURCES_FOLDER + "/data-generation-files";
    public static final String EXAMPLE_USERNAMES_FILE = DATA_GENERATION_FILES_FOLDER + "/example_usernames.txt";
    public static final String DEFAULT_AVATAR_FILE = RESOURCES_FOLDER + "/defaults/default-avatar.png";

    public static final String MEMORY_IMG_PATH = "/memory/img/";
    public static final String AVATAR = "/avatar";
    public static final String DEFAULT_AVATAR_FILENAME = "avatar.jpg";
    public static final String USER_IMAGE_PATH = "/portfolio/avatar/";
    public static final String NOT_AN_IMAGE_FILE = " is not an image file. Please upload an image file";
    public static final String DIRECTORY_CREATED = "Created directory for: ";
}
