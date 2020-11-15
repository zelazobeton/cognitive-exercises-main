package com.zelazobeton.cognitiveexercieses.constant;

public class FileConstants {
    public static final String LOCALHOST_ADDRESS = "http://localhost:8081";
    public static final String RESOURCES_FOLDER = System.getProperty("user.dir") + "/cognitive-exercises-data/src/main/resources";
    public static final String FORWARD_SLASH = "/";
    public static final String USER_FOLDER = RESOURCES_FOLDER + FORWARD_SLASH + "users-data";
    public static final String GAMES_DATA_FOLDER = RESOURCES_FOLDER + FORWARD_SLASH + "games-data";
    public static final String MEMORY_IMG_FOLDER = RESOURCES_FOLDER + FORWARD_SLASH + "memory-pictures";
    public static final String DATA_GENERATION_FILES_FOLDER = RESOURCES_FOLDER + FORWARD_SLASH + "data-generation-files";

    public static final String EXAMPLE_USERNAMES_FILE = DATA_GENERATION_FILES_FOLDER + FORWARD_SLASH + "example_usernames.txt";
    public static final String DEFAULT_AVATAR_FILE = RESOURCES_FOLDER + FORWARD_SLASH + "defaults/default-avatar.png";

    public static final String MEMORY_IMG_PATH = "/memory/img/";
    public static final String AVATAR = "avatar";
    public static final String DEFAULT_AVATAR_FILENAME = "avatar.jpg";
    public static final String USER_IMAGE_PATH = "/portfolio/avatar/";
    public static final String NOT_AN_IMAGE_FILE = " is not an image file. Please upload an image file";
    public static final String DIRECTORY_CREATED = "Created directory for: ";
}
