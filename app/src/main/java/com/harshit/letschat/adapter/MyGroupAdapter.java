package com.harshit.letschat.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.harshit.letschat.Firebase.MyDatabase;
import com.harshit.letschat.GroupChat;
import com.harshit.letschat.MyGroup;
import com.harshit.letschat.R;
import com.harshit.letschat.model.GroupList;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyGroupAdapter extends RecyclerView.Adapter<MyGroupAdapter.MyViewHolder> {

    private static final String TAG = "MyGroupAdapter";
    Context context;
    ArrayList<GroupList> groupList;
    String  groupAdmin = "";
    String groupId = "";
    Activity activity;

    public MyGroupAdapter(Context context, ArrayList<GroupList> lists, Activity activity) {
        this.context = context;
        this.groupList = lists;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_group_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        groupId = groupList.get(position).getId();;
        //searching group detail from every group Id
        MyDatabase.groupDetail().child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if(data.exists()) {
                    //forName
                    if(data.child("name").getValue() != null){
                        String groupName = data.child("name").getValue().toString();
                        holder.groupName.setText(groupName);
                    }
                    //forImage
                    if(data.child("image").getValue() != null){
                        String groupImage = data.child("image").getValue().toString(); //image in form of link
                        try {
                            Glide.with(context)
                                    .load(groupImage)
                                    .placeholder(R.drawable.logo)
                                    .into(holder.groupImage);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if(data.child("admin").getValue() != null) {
                        groupAdmin = data.child("admin").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(context, GroupChat.class);
                it.putExtra("groupId", groupId);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!groupAdmin.isEmpty() && groupAdmin.equals(FirebaseAuth.getInstance().getUid())) {
                    deletMessage();
                }
                return false;
            }
        });


//        holder.groupName.setText(groupList.get(position).getGroupName());
    }

    public void deletMessage( ) {
        new AlertDialog.Builder(activity)
                .setTitle("Delete Message")
                .setMessage("Dou you want to delete group permanently? ")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        deleteGropChat();
                        deleteGroupDetail();
                        deleteGroupFromEveryUser();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();

    }
    /*
    takes o(n) which is very time taken
    optimize soln is :
        iterate over every member from group
        delete group from their account
        it will take o(k);
    */


    private void deleteGropChat() {
        MyDatabase.groupChat().child(groupId).removeValue();
    }

    private void deleteGroupDetail() {
        MyDatabase.groupDetail().child(groupId).removeValue();
    }

    private void deleteGroupFromEveryUser() {
       MyDatabase.userGroup().addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()) {
                   Log.d(TAG,snapshot.getKey());

                   for(DataSnapshot myData : snapshot.getChildren()) {

                       if(myData.child(groupId).exists())
                           MyDatabase.userGroup().child(myData.getKey()).child(groupId).removeValue();
                   }

               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }


    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public void addNewItem(ArrayList<GroupList> lists) {
        this.groupList = lists;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView groupImage;
        TextView groupName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            groupImage = itemView.findViewById(R.id.groupImage);
            groupName = itemView.findViewById(R.id.groupName);
        }
    }
}
