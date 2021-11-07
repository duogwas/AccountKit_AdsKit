package hou.duogwas.hmsaccountkit;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "MyAccount";
    AccountAuthParams authParams;
    AccountAuthService authService;
    Button btn_SignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_SignIn=findViewById(R.id.btn_SignIn);
        btn_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call signinID method
                signInId();
            }
        });
    }

    ActivityResultLauncher<Intent> signInIDResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //Handle the results returned when logging in
                    Intent data = result.getData();
                    //id-token signIn
                    Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
                    if(authAccountTask.isSuccessful()){
                        // The sign-in is successful, and the user's ID information and ID token are obtained.
                        AuthAccount authAccount = authAccountTask.getResult();
                        Log.i(TAG, authAccount.getDisplayName() + " signIn success ");
                        Log.i(TAG,"idToken + {" + authAccount.getIdToken()+"}");
                        //Data transmission
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        //intent.putExtra("Displayname", authAccount.getDisplayName());
                        //intent.putExtra("ImageUrl", authAccount.getAvatarUriString());
                        startActivity(intent);
                        finish();
                    }else {
                        // The sign-in failed. No processing is required. Logs are recorded for fault locating.
                        Log.i(TAG,"sign in failed: " + ((ApiException)authAccountTask.getException()).getStatusCode());
                    }
                }
            });

    private void signInId(){
        authParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).
                setIdToken().setAccessToken().createParams();
        authService= AccountAuthManager.getService(LoginActivity.this,authParams);
        signInIDResult.launch(authService.getSignInIntent());
    }
}