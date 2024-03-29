package com.example.securemessagingapp.adapters;

import android.graphics.Bitmap;

import androidx.recyclerview.widget.RecyclerView;

import com.example.securemessagingapp.databinding.ItemContainerReceivedMessageBinding;
import com.example.securemessagingapp.databinding.ItemContainerSentMessageBinding;
import com.example.securemessagingapp.models.ChatMessage;

public class ChatAdapter {
    private final List<ChatMessage> chatMessage;
private final Bitmap receiverProfileImage;
private final String senderId;
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
static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
     private final ItemContainerReceivedMessageBinding binding;
     ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding)
     {
         super(itemContainerReceivedMessageBinding.getRoot());
         binding = itemContainerReceivedMessageBinding;
     }
     void setData(ChatMessage chatMessage , Bitmap receiverprofileImage)
     {
         binding.textMessage.setText(chatMessage.message);
         binding.textDateTime.setText(chatMessage.dateTime);
         binding.imageProfile.setImageBitmap(receiverprofileImage);
     }
}
}
