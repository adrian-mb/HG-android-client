package hg.hg_android_client.mainscreen.chat_client;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import hg.hg_android_client.R;

public class ChatDialog extends Dialog {

    public ChatDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        initializeButtonClose();
    }

    private void initializeButtonClose() {
        ImageButton b = (ImageButton) findViewById(R.id.button_close);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

}
