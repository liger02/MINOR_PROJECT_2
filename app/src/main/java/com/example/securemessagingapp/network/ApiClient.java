package com.example.securemessagingapp.network;

public class ApiClient {
    private static Retrofit retrofit=null;
    public static Retrofit getClient (){
        if(retrofit== null){
            retrofit =new Retrofit.Buider()
                    .baseUrl('https://fcm.googleapis.com/fcm/')
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

        }
        return retrofit;
    }
}
