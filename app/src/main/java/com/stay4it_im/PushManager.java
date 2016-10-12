package com.stay4it_im;

import android.content.Context;

import com.stay4it_im.entities.Message;

/**
 * Created by zhangchao_a on 2016/10/12.
 */

public class PushManager {

    private final Context context;
    private static PushManager mInstance;

    private PushManager(Context context) {

        this.context=context;
    }

    public static PushManager getInstance(Context context)
    {
        if (mInstance==null)
        {
            mInstance=new PushManager(context);
        }
        return mInstance;
    }

    public void handlePush(String content)
    {
        Message message=Message.test("0001","me","you");
//        Message message=new Message();
        PushChanger.getInstance().notifyChanged(message);
    }

    public void sendMessage(Message msg)
    {
//         Intent service=new Intent(context,PushService.class);
//         service.putExtra(Constants.KEY_MESSAGE,msg);
//         context.startService(service);
           msg.setStatus(Message.StatusType.ing);
           PushChanger.getInstance().notifyChanged(msg);
           msg.setStatus(Message.StatusType.done);
          PushChanger.getInstance().notifyChanged(msg);

    }

    public void addObservers(PushWatcher watcher)
    {
         PushChanger.getInstance().addObserver(watcher);
    }

    public void removeObservers(PushWatcher watcher)
    {
        PushChanger.getInstance().deleteObserver(watcher);
    }

    public void removeObservers()
    {
        PushChanger.getInstance().deleteObservers();
    }
}