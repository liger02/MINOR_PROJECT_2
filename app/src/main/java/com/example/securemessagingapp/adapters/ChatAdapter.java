package com.example.securemessagingapp.adapters;

import static com.example.securemessagingapp.methods.RSA.decrypt;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.securemessagingapp.activities.ChatActivity;
import com.example.securemessagingapp.databinding.ItemContainerReceivedMessageBinding;
import com.example.securemessagingapp.databinding.ItemContainerSentMessageBinding;
import com.example.securemessagingapp.models.ChatMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final List<ChatMessage> chatMessages;
    private  Bitmap receiverProfileImage;
    private final String senderId;
    private static String dcryprMessage;
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;
    public void setReceiverProfileImage(Bitmap bitmap)
    {
        receiverProfileImage = bitmap;
    }


    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String senderId) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SENT)
        {
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
        else
        {
            return new ReceivedMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==VIEW_TYPE_SENT)
        {
            ((SentMessageViewHolder)holder).setData(chatMessages.get(position));
        }
        else
        {
            ((ReceivedMessageViewHolder)holder).setData(chatMessages.get(position),receiverProfileImage);

        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
    public int getItemViewType(int position)
    {
        if(chatMessages.get(position).senderId.equals(senderId))
        {
            return VIEW_TYPE_SENT;

        }
        else
        {
           return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
         private  final ItemContainerSentMessageBinding binding;
         SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding)
         {
             super(itemContainerSentMessageBinding.getRoot());
             binding = itemContainerSentMessageBinding;
         }
         void setData(ChatMessage chatMessage)
         {
             binding.textMessage.setText(chatMessage.message);
             binding.textDateTime.setText(chatMessage.dateTime);
         }
    }
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
         private final ItemContainerReceivedMessageBinding binding;
         ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding)
         {
             super(itemContainerReceivedMessageBinding.getRoot());
             binding = itemContainerReceivedMessageBinding;
         }
         void setData(ChatMessage chatMessage , Bitmap receiverprofileImage)
         {
//             Authenticate authenticate = new Authenticate();

             System.out.println(chatMessage.isEncrypted);

             if(chatMessage.isEncrypted.equals("true")){
                 try {
                     System.out.println("pKey  "+chatMessage.privateKey);
                     System.out.println("mmmm  "+chatMessage.message);
                     dcryprMessage = decrypt(chatMessage.message, chatMessage.privateKey);
                     System.out.println("ddddd   "+dcryprMessage);
                     binding.textMessage.setText(chatMessage.message);

                 }catch (Exception e){
                     e.printStackTrace();
                 }
                 binding.textMessage.setOnClickListener(v->{
//                     authenticate.authenticateUser();

                         ChatActivity.isAuthenticated(new OnCompleteListener<Boolean>() {
                             @Override
                             public void onComplete(@NonNull Task< Boolean > task) {
                                 if (task.getResult()) {
                                     // Authentication successful

                                     binding.textMessage.setText(dcryprMessage);

                                 } else {
                                     // Authentication failed
//                                     Toast("Authentication Failed! Please try again.");
                                 }
                             }
                         });

                 });


             }else {
                 binding.textMessage.setText(chatMessage.message);

             }
             binding.textDateTime.setText(chatMessage.dateTime);
             if(receiverprofileImage != null)
             {
                 binding.imageProfile.setImageBitmap(receiverprofileImage);
             }
         }

    }
//    public static class Authenticate extends FragmentActivity {
//        // ...
//
//        private void authenticateUser() {
//            BiometricUtils.showBiometricPrompt(
//                    this,
//                    new BiometricPrompt.AuthenticationCallback() {
//                        @Override
//                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
//                            super.onAuthenticationError(errorCode, errString);
//                            // Handle error.
//                        }
//
//                        @Override
//                        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
//                            super.onAuthenticationSucceeded(result);
//                            // Authentication succeeded!
//                        }
//
//                        @Override
//                        public void onAuthenticationFailed() {
//                            super.onAuthenticationFailed();
//                            // User biometric rejected.
//                        }
//                    }
//            );
//        }
//    }

}
