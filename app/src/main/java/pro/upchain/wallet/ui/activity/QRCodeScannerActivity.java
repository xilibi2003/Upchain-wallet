package pro.upchain.wallet.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Vibrator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.gyf.barlibrary.ImmersionBar;

import java.util.List;

import butterknife.BindView;
import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.utils.LogUtils;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class QRCodeScannerActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener, QRCodeView.Delegate {


    private static final String TAG = QRCodeScannerActivity.class.getSimpleName();

    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;

    private static final int QRCODE_RESULT = 124;


    Toolbar scannerToolbar;

    private RelativeLayout rlFlashLight;
    private LinearLayout llBack;

    ZXingView mZXingView;
    private ZXingView zxingview;
    private LinearLayout llyBack;
    private Toolbar scannerToolsbar;

    @Override
    public void initView() {

        zxingview = findViewById(R.id.zxingview);
        llyBack = findViewById(R.id.lly_back);
        rlFlashLight = findViewById(R.id.rl_flash_light);
        scannerToolsbar = findViewById(R.id.scanner_toolsbar);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_qrcode_scanner;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        mZXingView.setDelegate(this);

//        BGAQRCodeUtil.setDebug(true);
    }

    @Override
    public void configViews() {
        ImmersionBar.with(this)
                .titleBar(scannerToolbar, false)
                .navigationBarColor(R.color.colorPrimary)
                .init();


        rlFlashLight =  findViewById(R.id.rl_flash_light);
        rlFlashLight.setOnClickListener(this);

        llBack =  findViewById(R.id.lly_back);
        llBack.setOnClickListener(this);


        findViewById(R.id.choose_qrcde_from_gallery).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lly_back:
                finish();
                break;
//            case R.id.choose_qrcde_from_gallery:
//            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
//                    .cameraFileDir(null)
//                    .maxChooseCount(1)
//                    .selectedPhotos(null)
//                    .pauseOnScroll(false)
//                    .build();
//            startActivityForResult(photoPickerIntent, REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
//            break;

            case R.id.rl_flash_light:

                mZXingView.openFlashlight();

                break;
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        mZXingView.startCamera();
        mZXingView.startSpotAndShowRect();   // 显示扫描框，并开始识别
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }


    @Override
    protected void onStart() {
        super.onStart();

        requestCodeQRCodePermissions();

        mZXingView.startCamera();

        mZXingView.changeToScanQRCodeStyle();
        mZXingView.setType(BarcodeType.ONLY_QR_CODE, null); // 只识别 QR_CODE
        mZXingView.startSpotAndShowRect();   // 显示扫描框，并开始识别
    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == Activity.RESULT_OK && requestCode == PictureConfig.CHOOSE_REQUEST) {
//            List<LocalMedia> localMedias = PictureSelector.obtainMultipleResult(data);
//            path = localMedias.get(0).getPath();
//            // 解析获取的图片中的二维码
//            new DecodeAsyncTask().execute(path);
//        }
//    }


    public void onScanQRCodeSuccess(String result) {

        vibrate();

        LogUtils.d(result);

        Intent intent = new Intent();
        intent.putExtra("scan_result", result);

        setResult(RESULT_OK, intent);
        finish();

    }

    /**
     * 摄像头环境亮度发生变化
     *
     * @param isDark 是否变暗
     */
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        String tipText = mZXingView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZXingView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZXingView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    /**
     * 处理打开相机出错
     */
    public void onScanQRCodeOpenCameraError() {
        LogUtils.e(TAG, "打开相机出错");
    }

}
