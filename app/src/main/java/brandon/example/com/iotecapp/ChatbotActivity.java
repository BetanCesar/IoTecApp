package brandon.example.com.iotecapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class ChatbotActivity extends AppCompatActivity implements AIListener{

    private AIService aiService;
    private AIDataService aiDataService;

    //Nuevo
    ListView listView;
    EditText editText;
    List<ChatModel> list_chat = new ArrayList<>();
    FloatingActionButton btn_send_message;
    CustomAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        final AIConfiguration config = new AIConfiguration("29f3d2469c1b4a28be69605bc46e055f",
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);

        aiDataService = new AIDataService(config);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        listView = (ListView) findViewById(R.id.list_message);
        editText = (EditText) findViewById(R.id.user_message);
        btn_send_message = (FloatingActionButton) findViewById(R.id.fab);
        adapter = new CustomAdapter(this, R.layout.list_item_message_recv, new ArrayList<ChatModel>());

        listView.setAdapter(adapter);
        btn_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();
                //ChatModel model = new ChatModel(text,true);
                //list_chat.add(model);
                //adapter.add(new ChatModel(text,false));
                new SendRequesTask(aiDataService).execute(text);
            }
        });

    }


    public class SendRequesTask extends AsyncTask<String, String, AIResponse> {

        private AIDataService aiDataService;

        public SendRequesTask(AIDataService aiDataService) {
            this.aiDataService = aiDataService;
        }

        @Override
        protected AIResponse doInBackground(String... strings) {
            AIRequest aiRequest = new AIRequest();
            AIResponse aiResponse = null;

            try{
                aiRequest.setQuery(strings[0]);
                aiResponse = aiDataService.request(aiRequest);


            }catch (AIServiceException ex){
                ex.printStackTrace();
            }

            return aiResponse;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //CODIGO PROGRESS DIALOG
        }

        @Override
        protected void onPostExecute(AIResponse aiResponse) {
            super.onPostExecute(aiResponse);
            Result result = aiResponse.getResult();
            //IMPRIMIR TEXTO
            adapter.add(new ChatModel(result.getResolvedQuery(),true));
            adapter.add(new ChatModel(result.getFulfillment().getSpeech(),false));
            adapter.notifyDataSetChanged();
            //textView.append("You: "+result.getResolvedQuery() + "\r\n" );

        }
    }


    @Override
    public void onResult(AIResponse result1) {
    }

    @Override
    public void onError(final AIError error) {
    }

    @Override
    public void onListeningStarted() {}

    @Override
    public void onListeningCanceled() {}

    @Override
    public void onListeningFinished() {}

    @Override
    public void onAudioLevel(final float level) {}
}

