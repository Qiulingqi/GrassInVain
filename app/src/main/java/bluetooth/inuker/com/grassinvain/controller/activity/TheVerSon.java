package bluetooth.inuker.com.grassinvain.controller.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import bluetooth.inuker.com.grassinvain.R;

public class TheVerSon extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_ver_son);
        initView();
    }

    private void initView() {
        LinearLayout back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView the_verson= (TextView) findViewById(R.id.the_verson);
        the_verson.setText("徒然草 "+getVersion());
    }


    public String getVersion(){
        try {
            PackageManager pack=TheVerSon.this.getPackageManager();
            PackageInfo info=pack.getPackageInfo(TheVerSon.this.getPackageName(),0);
            String version=info.versionName;
            return version;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";

    }

}
