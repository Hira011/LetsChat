package com.harshit.letschat.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

 public class MyDatabase {

    static DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static DatabaseReference publicDetail() {
        return mRef.child("users").child("detail");
    }
//    user detail
    public static DatabaseReference userDetail() {
        return mRef.child("users").child("detail").child(mAuth.getUid());
    }

//    universal chat reference
    public static DatabaseReference universalChatRef() {
        return mRef.child("users").child("universal").child("message");
    }

//   myGroup
    public static DatabaseReference myGroups(){
        return mRef.child("users").child("group").child(mAuth.getUid());
    }
//   group detail
    public static DatabaseReference groupDetail(){
        return mRef.child("groups").child("detail");
    }

//  group chat
    public static DatabaseReference groupChat(){
        return mRef.child("groups").child("chats");
    }

    public static DatabaseReference userGroup() {
        return mRef.child("users").child("group");
    }

    public static DatabaseReference personalSenderChat(String receiverId) {
        return mRef.child("personal").child(mAuth.getUid()).child(receiverId);
    }
     public static DatabaseReference personalReceiverChat(String receiverId) {
         return mRef.child("personal").child(receiverId).child(mAuth.getUid());
     }



 }
