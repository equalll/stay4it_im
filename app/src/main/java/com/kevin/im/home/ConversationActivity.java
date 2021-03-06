package com.kevin.im.home;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.kevin.im.BaseActivity;
import com.kevin.im.IMApplication;
import com.kevin.im.R;
import com.kevin.im.adapter.ConversationAdapter;
import com.kevin.im.db.ConversationController;
import com.kevin.im.entities.Conversation;
import com.kevin.im.entities.Message;
import com.kevin.im.net.AppException;
import com.kevin.im.net.JsonCallback;
import com.kevin.im.net.Request;
import com.kevin.im.net.RequestManager;
import com.kevin.im.push.IMPushManager;
import com.kevin.im.push.PushWatcher;
import com.kevin.im.util.Constants;
import com.kevin.im.util.UrlHelper;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import java.util.ArrayList;

/**
 * Created by zhangchao_a on 2016/10/14.
 */

public class ConversationActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ConversationAdapter mConversationAdapter;
    private ListView mConversationLsv;
    private ArrayList<Conversation> mConversationList;

    private PushWatcher watcher=new PushWatcher(){
        @Override
        public void onMessageReceived(Message message) {
            Conversation conversation = message.copyTo();
            mConversationList.remove(conversation);
            mConversationList.add(0,conversation);
            mConversationAdapter.setData(mConversationList);
            mConversationAdapter.notifyDataSetChanged();
        }

        @Override
        public void onMessageUpdated(Message oldMessage, Message newMessage) {
            Conversation conversation=oldMessage.copyTo();
            mConversationList.remove(conversation);
            if (newMessage!=null)
                mConversationList.add(0,newMessage.copyTo());
            else
                mConversationList.add(conversation);
            mConversationAdapter.setData(mConversationList);
            mConversationAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_conversation);
//        XGPushManager.registerPush(getApplicationContext());
        // 注册接口
        XGPushManager.registerPush(getApplicationContext(),
                new XGIOperateCallback() {
                    @Override
                    public void onSuccess(Object data, int flag) {
                        Log.w("TPush",
                                "+++ register push sucess. token:" + data);

                    }

                    @Override
                    public void onFail(Object data, int errCode, String msg) {
                        Log.w("TPush",
                                "+++ register push fail. token:" + data
                                        + ", errCode:" + errCode + ",msg:"
                                        + msg);


                    }
                });

        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,"G5qSjxqYGExjhFQtKFPBauEM");
    }

    @Override
    public void initView() {
        mConversationLsv=(ListView)findViewById(R.id.mConversationLsv);
        mConversationLsv.setOnItemClickListener(this);
        mConversationAdapter=new ConversationAdapter(this,mConversationList);
        mConversationLsv.setAdapter(mConversationAdapter);
    }

    @Override
    public void initData() {
        loadDataFromDB();
        loadDataFromServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IMPushManager.getInstance(getApplicationContext()).addObservers(watcher);

    }

    @Override
    protected void onPause() {
        super.onPause();
        IMPushManager.getInstance(getApplicationContext()).addObservers(watcher);

    }

    private void loadDataFromDB() {
        mConversationList=ConversationController.queryAllByTimeDesc();
        mConversationAdapter.setData(mConversationList);
        mConversationAdapter.notifyDataSetChanged();
    }

    private void loadDataFromServer() {
        Request request=new Request(UrlHelper.loadConversation());
        request.addHeader("Content-type","application/json");
        request.addHeader("Authorization", IMApplication.getToken());
        request.setCallback(new JsonCallback<ArrayList<Message>>() {

            @Override
            public ArrayList<Message> onPostRequest(ArrayList<Message> messages) {
                for (Message message:messages) {
                    ConversationController.syncMessage(message);
//                    Trace.d(message.toString());
                }
                return messages;
            }

            @Override
            public void onSuccess(ArrayList<Message> messages) {
                if (messages!=null&&messages.size()>0) {
                    notifyDataChanged();
                }
            }

            @Override
            public void onFailuer(AppException error) {
                error.printStackTrace();
            }
        });
        RequestManager.getInstance().performRequest(request);
    }

    private void notifyDataChanged() {
        loadDataFromDB();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XGPushManager.unregisterPush(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(this,ChatActivity.class);
        Conversation conversation=mConversationList.get(position);
        intent.putExtra(Constants.KEY_TARGETID,conversation.getTargetId());
        intent.putExtra(Constants.KEY_TARGETNAME,conversation.getTargetName());
        startActivity(intent);
    }
}
