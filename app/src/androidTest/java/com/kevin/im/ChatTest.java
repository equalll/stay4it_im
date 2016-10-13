package com.kevin.im;

import android.support.test.runner.AndroidJUnit4;

import com.kevin.im.push.PushManager;
import com.kevin.im.push.PushWatcher;
import com.kevin.im.entities.Message;
import com.kevin.im.util.Trace;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getContext;

/**
 * Created by zhangchao_a on 2016/10/12.
 */

@RunWith(AndroidJUnit4.class)
public class ChatTest{
    public static final String SELFID="Stay";
    public static final String TARGETID="Will";

    PushWatcher watcher1=new PushWatcher(){
        @Override
        public void messageUpdata(Message message) {
            Trace.d(message.getStatus()+"");
//            Log.e("lalala",message.getStatus()+"");
        }
    };
    PushWatcher watcher2=new PushWatcher();


    @Before
    public void setUp() throws Exception {
        PushManager.getInstance(getContext()).addObservers(watcher1);
    }

    @Test
    public void testSendMsg() throws Exception
    {
        Message message=Message.test("0001",SELFID,TARGETID);
        PushManager.getInstance(getContext()).sendMessage(message);
    }

    @Test
    public void testReceiveMsg() throws Exception
    {
        PushManager.getInstance(getContext()).handlePush("");
    }

    @After
    public void tearDown() throws Exception {
        PushManager.getInstance(getContext()).removeObservers(watcher1);
    }
}