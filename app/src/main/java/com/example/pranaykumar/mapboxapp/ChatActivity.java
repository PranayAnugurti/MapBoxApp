package com.example.pranaykumar.mapboxapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    ImageButton sendBtn;
    EditText msgField;
    RecyclerView recyclerView;
    String user1,user2;
    MessageAdapter mAdapter;
    private List<Message>messageList=new ArrayList<>();
    private List<MsgSender>senderList=new ArrayList<>();
    DatabaseReference ref1,ref2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sendBtn=findViewById(R.id.send_button);
        msgField=findViewById(R.id.message_input);

        recyclerView=findViewById(R.id.messages);
        mAdapter=new MessageAdapter(messageList,senderList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        

        
        Intent intent=getIntent();
        user1=intent.getStringExtra("userId");
        user2=intent.getStringExtra("chatWith");
        ref1=FirebaseDatabase.getInstance().getReference("messages/"+setOneToOneChat(user1,user2));
        ref2=FirebaseDatabase.getInstance().getReference("messages/"+user2+"_"+user1);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = msgField.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", user1);
                    map.put("time",getCurrentTime(v));
                    ref1.push().setValue(map);
                }
            }
        });

        ChildEventListener childEventListener = ref1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                String message = dataSnapshot.child("message").getValue().toString();
                String userName = dataSnapshot.child("user").getValue().toString();
                String time =dataSnapshot.child("time").getValue().toString();


                if (userName==user1) {
                    messageList.add(new Message(message,time,userName,"me"));
                    senderList.add(new MsgSender("me"));
                } else {
                    messageList.add(new Message(message,time,userName,"other"));
                    senderList.add(new MsgSender("other"));
                }
                mAdapter.notifyItemInserted(messageList.size() - 1);
                scrollToBottom();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref1.addChildEventListener(childEventListener);
    }


    private String setOneToOneChat(String uid1, String uid2)
    {

//Check if user1’s id is less than user2's

        if(uid1.compareTo(uid2)<0){

            return uid1+"_"+uid2;
        }
        else{

            return uid2+"_"+uid1;
        }
    }
    public String getCurrentTime(View view) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }
    private void scrollToBottom() {
        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }
}
