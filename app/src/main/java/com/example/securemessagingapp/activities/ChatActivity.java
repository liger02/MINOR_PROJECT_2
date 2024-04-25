package com.example.securemessagingapp.activities;

import static com.example.securemessagingapp.methods.RSA.encrypt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.securemessagingapp.adapters.ChatAdapter;
import com.example.securemessagingapp.databinding.ActivityChatBinding;
import com.example.securemessagingapp.models.ChatMessage;
import com.example.securemessagingapp.models.User;
import com.example.securemessagingapp.network.ApiClient;
import com.example.securemessagingapp.network.ApiService;
import com.example.securemessagingapp.utilities.Constants;
import com.example.securemessagingapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Callback;
import retrofit2.Response;


public class ChatActivity extends BaseActivity {
    public static ActivityChatBinding binding;
    private User receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private static PreferenceManager preferenceManager;
    private static FirebaseFirestore database;
    private String conversionID=null;
    private Boolean isReceiverAvailable=false;
    private String encryptedMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.authWindow.setVisibility(View.GONE);
        setListeners();
        loadReceiverDetails();
        init();
        listenMessages();
    }
    private void init()
    {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                getBitmapFromEncodedString(receiverUser.image),
                preferenceManager.getString(Constants.KEY_USER_ID)


        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(receiverUser.id).addSnapshotListener(ChatActivity.this,(value,error)-> {
                    if (error != null) {
                        return;
                    }
                    if (value != null) {
                        receiverUser.publicKey = value.getString(Constants.PUBLIC_KEY);
                        receiverUser.privateKey = value.getString(Constants.PRIVATE_KEY);
                    }
                });

    }

    private void sendMessage()
    {
        HashMap<String,Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID , preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID,receiverUser.id);
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        message.put(Constants.IS_ENCRYPTED,"false");
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if(conversionID != null){
            updateConversion(binding.inputMessage.getText().toString());
        }else {
            HashMap<String,Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME,preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_SENDER_IMAGE,preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_RECEIVER_ID,receiverUser.id);
            conversion.put(Constants.KEY_RECEIVER_NAME,receiverUser.name);
            conversion.put(Constants.KEY_RECEIVER_IMAGE,receiverUser.image);
            conversion.put(Constants.KEY_LAST_MESSAGE,binding.inputMessage.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP,new Date());
            conversion.put(Constants.IS_ENCRYPTED,"false");
            addConversion(conversion);
        }
        if(!isReceiverAvailable)
        {
            try
            {
                JSONArray tokens = new JSONArray();
                tokens.put(receiverUser.token);
                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
                data.put(Constants.KEY_NAME,preferenceManager.getString(Constants.KEY_NAME));
                data.put(Constants.KEY_FCM_TOKEN,preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                data.put(Constants.KEY_MESSAGE,binding.inputMessage.getText().toString());

                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA,data);
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens);

                sendNotification(body.toString());
            }
            catch (Exception exception)
            {
                showToast(exception.getMessage());
            }
        }
        binding.inputMessage.setText(null);

    }

    private void sendEncrptedMessage()
    {
        try {

            String SpublicKey = preferenceManager.getString(Constants.PUBLIC_KEY);




            System.out.println("sender  "+SpublicKey);
            System.out.println("rkey  "+receiverUser.publicKey);
            System.out.println("rPKey  "+receiverUser.privateKey);


            if (receiverUser.publicKey == null ) {
               // Log.e(TAG, "Error: Public key is null or message is empty");
                System.out.println("Errorr: public key is null");

                return;
            }
            encryptedMessage = encrypt(binding.inputMessage.getText().toString(), receiverUser.publicKey);
        }catch (Exception e){
            e.printStackTrace();
        }
        HashMap<String,Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID , preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID,receiverUser.id);
        message.put(Constants.KEY_MESSAGE, encryptedMessage);
        message.put(Constants.KEY_TIMESTAMP, new Date());
        message.put(Constants.IS_ENCRYPTED,"true");
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if(conversionID != null){
            updateConversion(encryptedMessage);
        }else {
            HashMap<String,Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME,preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_SENDER_IMAGE,preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_RECEIVER_ID,receiverUser.id);
            conversion.put(Constants.KEY_RECEIVER_NAME,receiverUser.name);
            conversion.put(Constants.KEY_RECEIVER_IMAGE,receiverUser.image);
            conversion.put(Constants.KEY_LAST_MESSAGE,encryptedMessage);
            conversion.put(Constants.KEY_TIMESTAMP,new Date());
            conversion.put(Constants.IS_ENCRYPTED,"true");
            addConversion(conversion);
        }
        if(!isReceiverAvailable)
        {
            try
            {
                JSONArray tokens = new JSONArray();
                tokens.put(receiverUser.token);
                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
                data.put(Constants.KEY_NAME,preferenceManager.getString(Constants.KEY_NAME));
                data.put(Constants.KEY_FCM_TOKEN,preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                data.put(Constants.KEY_MESSAGE,encryptedMessage);

                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA,data);
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens);

                sendNotification(body.toString());
            }
            catch (Exception exception)
            {
                showToast(exception.getMessage());
            }
        }
        binding.inputMessage.setText(null);

    }
    private void showToast(String message)
    {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

private void sendNotification(String messageBody)
{
    ApiClient.getClient().create(ApiService.class).sendMessage(
            Constants.getRemoteMsgHeaders(),
            messageBody

    ).enqueue(new Callback (){
        public void onResponse(@NonNull retrofit2.Call call,@NonNull Response response)
        {
            if(response.isSuccessful())
            {
                try
                {
                    if (response.body()!=null)
                    {
                        String responseBody = response.body().toString();
                        JSONObject responseJson = new JSONObject(responseBody);
                        JSONArray results = responseJson.getJSONArray("results");
                        if (responseJson.getInt("failure")==1)
                        {
                            JSONObject error = (JSONObject) results.get(0);
                            showToast(error.getString("error"));
                            return;
                        }
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                showToast("Notification sent Successfully");
            }
            else
            {
                showToast("Error"+response.code());
            }
        }
        public void onFailure(@NonNull retrofit2.Call call,@NonNull Throwable t)
        {
            showToast(t.getMessage());
        }
    });
}
    private void listenAvailabitlityOfReceiver(){
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(receiverUser.id).addSnapshotListener(ChatActivity.this,(value,error)->{
            if(error!=null){
                return;
            }
            if(value!=null){
                if(value.getLong(Constants.KEY_AVAILABILITY)!=null){
                    int availability= Objects.requireNonNull(
                            value.getLong(Constants.KEY_AVAILABILITY)
                    ).intValue();
                    isReceiverAvailable=availability ==1;
                }
                receiverUser.token = value.getString(Constants.KEY_FCM_TOKEN);
                if(receiverUser.image == null)
                {
                    receiverUser.image = value.getString(Constants.KEY_IMAGE);
                    chatAdapter.setReceiverProfileImage(getBitmapFromEncodedString(receiverUser.image));
                    chatAdapter.notifyItemRangeChanged(0,chatMessages.size());
                }
            }
            if (isReceiverAvailable){
                binding.textAvailability.setVisibility(View.VISIBLE);
            }else{
                binding.textAvailability.setVisibility(View.GONE);
            }
        });
    }
    private void listenMessages()
    {
        /*HashMap<String , Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID , preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
        message.put(Constants.KEY_MESSAGE , binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        binding.inputMessage.setText(null);*/
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID , preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,receiverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }
    private final EventListener<QuerySnapshot> eventListener = (value, error) ->
    {
        if(error!=null)
        {
            return;
        }
        if(value !=null)
        {
            int count = chatMessages.size();
            for(DocumentChange documentChange : value.getDocumentChanges())
            {
                if(documentChange.getType()==DocumentChange.Type.ADDED)
                {

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessage.isEncrypted=documentChange.getDocument().getString(Constants.IS_ENCRYPTED);
                    chatMessage.privateKey = preferenceManager.getString(Constants.PRIVATE_KEY);
                    System.out.println(receiverUser.privateKey);
                    System.out.println(chatMessage.privateKey);
                    chatMessages.add(chatMessage);


                }
            }
            Collections.sort(chatMessages,(obj1,obj2)-> obj1.dateObject.compareTo(obj2.dateObject));
            if(count ==0)
            {
                chatAdapter.notifyDataSetChanged();
            }
            else
            {
              chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
              binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size()-1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
        if(conversionID == null){
            checkForConversion();
        }
    } ;
    private Bitmap getBitmapFromEncodedString(String encodedImage)
    {
        if(encodedImage!=null)
        {
            byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }
        else
        {
            return null;
        }
    }

    private void loadReceiverDetails()
    {
        receiverUser = (User)getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUser.name);
    }

    private void setListeners()
    {
        binding.imageBack.setOnClickListener(v->onBackPressed());
       // binding.layoutSend.setOnClickListener(v -> sendMessage());
        binding.layoutSend.setOnClickListener(x->{
            if(binding.encrypted.isChecked()==true){
//                binding.layoutSend.setOnClickListener(v->sendEncrptedMessage());
                sendEncrptedMessage();
            }

            else{
//                binding.layoutSend.setOnClickListener(v -> sendMessage());
                sendMessage();
            }
        });

    }
    private String getReadableDateTime(Date date)
    {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
    private void addConversion(HashMap<String,Object> conversion){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionID=documentReference.getId());
    }

    private void updateConversion(String message){
        DocumentReference documentReference=
                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionID);
            documentReference.update(
          Constants.KEY_LAST_MESSAGE,message,
                    Constants.KEY_TIMESTAMP,new Date()
            );
}
    public void checkForConversion(){
        if(chatMessages.size() != 0){
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    receiverUser.id
            );
            checkForConversionRemotely(
                    receiverUser.id,
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }
    private void checkForConversionRemotely(String senderId,String receiverId){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }


    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if(task.isSuccessful() && task.getResult() !=null && task.getResult().getDocuments().size()> 0){
            DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
            conversionID = documentSnapshot.getId();
        }
    };

    public static boolean isAuthenticated(){
        AtomicBoolean b = new AtomicBoolean(false);
        binding.authWindow.setVisibility(View.VISIBLE);
        binding.setauth.setOnClickListener(v->{
            System.out.println("Enterinhgyfyjukytr");
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.KEY_USER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size()>0) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            if(documentSnapshot.getString(Constants.KEY_PASSWORD).equals(binding.checkpass.getText().toString())){
                                b.set(true);
                            }
                        }

                    });
            System.out.println("bbbbbbbbb  "+b.get()+"    "+b);
            System.out.println("passw   "+binding.checkpass.getText().toString());

        });


        return b.get();
    }

    @Override
    protected void onResume() {
        super.onResume();
  listenAvailabitlityOfReceiver();
    }
}