/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pro.upchain.wallet.view.loadding;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pro.upchain.wallet.R;


public class CustomDialog extends Dialog {

    private TextView tvProgress;
    private LoadingView loadingView;

    public CustomDialog(Context context) {
        this(context, 0);
    }

    public CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private View inflateView(Context ct) {
        View v = View.inflate(ct, R.layout.common_progress_view, null);
        loadingView = (LoadingView)v.findViewById(R.id.loadingView);
        tvProgress = (TextView)v.findViewById(R.id.tv_progress);
        return v;
    }

    public static CustomDialog instance(Context context) {
        CustomDialog dialog = new CustomDialog(context, R.style.loading_dialog);
        View v = dialog.inflateView(context);
        dialog.setContentView(v,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
        );
        return dialog;

    }

    public void setTvProgress(String progressTip) {
        tvProgress.setText(progressTip);
    }
}

