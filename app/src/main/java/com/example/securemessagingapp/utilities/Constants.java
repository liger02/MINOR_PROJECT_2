package com.example.securemessagingapp.utilities;

import java.util.HashMap;

public class  Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_NAME = "chatAppPrefence";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER="user";
    public static final String KEY_COLLECTION_CHAT = "chat";

    public static final String KEY_SENDER_ID= "senderId";
    public static final String KEY_RECEIVER_ID= "receiverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP="timestamp";
    public static final String KEY_COLLECTION_CONVERSATIONS="conversations";
    public static final String KEY_SENDER_NAME="senderName";
    public static final String KEY_RECEIVER_NAME="receiverName";
    public static final String KEY_SENDER_IMAGE="senderImage";
    public static final String KEY_RECEIVER_IMAGE="receiverImage";
    public static final String KEY_LAST_MESSAGE="lastMessage";
    public static final String KEY_AVAILABILITY="availability";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static final String PRIVATE_KEY = "private_key";
    public static final String PUBLIC_KEY = "public_key";
    public static  HashMap<String,String>remoteMsgHeaders = null;
    public static HashMap<String,String> getRemoteMsgHeaders()
    {
        if(remoteMsgHeaders ==null)
        {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "Key=AAAA-MdRmnY:APA91bEz4zM34HMQ9UidZ3TuOMg9xJT7wO0Dr88mGwBpYA6sJqGSE57o_ee_qf5n68hsiNh87ZCDCoeUeisTxH0uOcv52hFDPGAtHwAJSccSgV0_tacd-cI-UOuOJihF2fhdE-iPBOkj"

            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );

        }
        return remoteMsgHeaders;
    }

}
