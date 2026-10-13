package com.zfdang.jsmth_android;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by zfdang on 2016-5-10.
 */
public class SMTHBaseActivity extends AppCompatActivity {
  protected ProgressDialog pDialog = null;
  protected final CompositeDisposable mDisposable = new CompositeDisposable();

  // http://stackoverflow.com/questions/22924825/view-not-attached-to-window-manager-crash
  public void showProgress(String message) {
    if (pDialog == null) {
      pDialog = new ProgressDialog(this, R.style.PDialog_MyTheme);
      pDialog.setCancelable(true);
      pDialog.setCanceledOnTouchOutside(false);
    }
    pDialog.setMessage(message);
    pDialog.show();
  }

  public void dismissProgress() {
    if (pDialog != null && pDialog.isShowing()) {
      pDialog.dismiss();
    }
  }

  @Override protected void onDestroy() {
    mDisposable.clear();
    super.onDestroy();

    dismissProgress();
  }

  @Override public Resources getResources() {
    Resources res = super.getResources();
    Configuration config = res.getConfiguration();
    config.fontScale = Settings.getInstance().getFontSizeFloatValue();
    res.updateConfiguration(config, res.getDisplayMetrics());

    //        Log.d("SMTHBaseActivity", "getResources: " + config.fontScale);
    return res;
  }
}
