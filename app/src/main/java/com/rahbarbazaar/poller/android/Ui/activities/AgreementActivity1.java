package com.rahbarbazaar.poller.android.Ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import com.rahbarbazaar.poller.android.R;


public class AgreementActivity1 extends AppCompatActivity {

  CheckBox checkbox_agreement;
  ImageView img_page_icon_rules,img_rules_enter_icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement1);

//        if (LocaleManager.getLocale(getResources()).getLanguage().equals("fa")) {
//
//            Typeface font = TypeFaceGenerator.getInstance().getByekanFont(this);
//        }

        initview();


        checkbox_agreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    img_rules_enter_icon.setVisibility(View.VISIBLE);
                    img_page_icon_rules.setVisibility(View.GONE);
                }else{
                    img_rules_enter_icon.setVisibility(View.GONE);
                    img_page_icon_rules.setVisibility(View.VISIBLE);
                }

            }
        });

        if(checkbox_agreement.isChecked()){
            img_rules_enter_icon.setVisibility(View.VISIBLE);
            img_page_icon_rules.setVisibility(View.GONE);
        }else{
            img_page_icon_rules.setVisibility(View.VISIBLE);
            img_rules_enter_icon.setVisibility(View.GONE);
        }

    }

    private void initview() {
        checkbox_agreement=findViewById(R.id.checkbox_agreement);
        img_page_icon_rules=findViewById(R.id.img_page_icon_rules);
        img_rules_enter_icon=findViewById(R.id.img_rules_enter_icon);
    }
}
