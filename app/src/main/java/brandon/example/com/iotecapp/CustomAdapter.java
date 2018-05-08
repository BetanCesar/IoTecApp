package brandon.example.com.iotecapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.github.library.bubbleview.BubbleTextView;

import java.util.List;

/**
 * Created by brandon on 14/03/18.
 */

public class CustomAdapter extends ArrayAdapter<ChatModel>{


    private Context context;


    public CustomAdapter(@NonNull Context context, int resource, @NonNull List<ChatModel> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View view = null;
        convertView = null;

        ChatModel chatModel = getItem(i);
        if(convertView == null){
            if(chatModel.isSend()) {
                view = LayoutInflater.from(context).inflate(R.layout.list_item_message_send,parent,false);
            }else {
                view = LayoutInflater.from(context).inflate(R.layout.list_item_message_recv,parent,false);
            }

        }


        BubbleTextView text_message = (BubbleTextView) view.findViewById(R.id.text_message);
        text_message.setText(chatModel.getMessage());
        return view;

    }
}