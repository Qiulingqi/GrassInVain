package bluetooth.inuker.com.grassinvain.controller.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pingplusplus.android.Pingpp;

import java.util.ArrayList;
import java.util.List;

import bluetooth.inuker.com.grassinvain.R;
import bluetooth.inuker.com.grassinvain.controller.MyApplication;
import bluetooth.inuker.com.grassinvain.controller.adapter.OrderListAdapter;
import bluetooth.inuker.com.grassinvain.controller.personinformation.TheGoodsAddress;
import bluetooth.inuker.com.grassinvain.network.body.HttpUtils;
import bluetooth.inuker.com.grassinvain.network.body.UserInfo;
import bluetooth.inuker.com.grassinvain.network.body.request.PayOrderDto;
import bluetooth.inuker.com.grassinvain.network.body.request.SubmitOrderBody;
import bluetooth.inuker.com.grassinvain.network.body.response.AddressDetailBody;
import bluetooth.inuker.com.grassinvain.network.body.response.MorenAddressBody;
import bluetooth.inuker.com.grassinvain.network.body.response.PayRecBody;
import bluetooth.inuker.com.grassinvain.network.body.response.ProductSDeatilBody;
import bluetooth.inuker.com.grassinvain.network.model.UserModel;
import bluetooth.inuker.com.grassinvain.network.model.callback.Callback;

import static java.lang.Integer.parseInt;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener {


    private UserModel userModel;
    private TextView huiyuan_zhuanxiang;
    private TextView shiji_price;
    private TextView xuyao_zhifu_price;
    private RecyclerView order_list;
    private TextView moren_xuanzhong_address;
    private RelativeLayout user_address;
    private LinearLayout back;
    private List<ProductSDeatilBody> data = new ArrayList<>();
    private List<ProductSDeatilBody> list = new ArrayList<>();
    private List<ProductSDeatilBody> product;
    private MorenAddressBody morenAddressBody1;
    private Dialog mCameraDialog;
    private ImageView zhifubao_src;
    private ImageView weixin_src;
    private TextView moren_name;
    private TextView moren_phone;

    private int requestCode = 0;
    private int shijiprice;
    private int zongjai;
    private int type=1;
    int zhuangtai=0;
    MyApplication mapp;

    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        userModel = new UserModel(this);
        Intent intent = getIntent();
        product = (List<ProductSDeatilBody>) intent.getSerializableExtra("product");
        data.addAll(product);
        initData();
        initView();
        mapp=(MyApplication) this.getApplication();
    }

    private void initView() {
        //返回事件
        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(this);
        // 地址选择
        user_address = (RelativeLayout) findViewById(R.id.user_address);
        user_address.setOnClickListener(this);
        // 设置默认姓名
        moren_name = (TextView) findViewById(R.id.textView23);
        // 设置默认电话
        moren_phone = (TextView) findViewById(R.id.textView28);
        // 设置提交地址
        moren_xuanzhong_address = (TextView) findViewById(R.id.moren_xuanzhong_address);
        //订单列表
        order_list = (RecyclerView) findViewById(R.id.order_list);

        // 商品合计总价
        xuyao_zhifu_price = (TextView) findViewById(R.id.xuyao_zhifu_price);
        zongjai = 0;
        for (int i = 0; i < data.size(); i++) {
            int count = Integer.parseInt(data.get(i).count);
            int price = parseInt(data.get(i).productFormatPrice);
            zongjai += (count * price);
        }
        String s = zongjai + "";
        StringBuilder sb2 = new StringBuilder(s);
        sb2.insert(s.length() - 2, ".");
        String s1 = sb2.toString();
        xuyao_zhifu_price.setText(s1);
        //添加横线 表示此价格无效
        xuyao_zhifu_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        //商品实际需要付款的金额
        shiji_price = (TextView) findViewById(R.id.textView16);
        shijiprice = 0;
        // 提交订单
        TextView submitOrder = (TextView) findViewById(R.id.textView15);
        submitOrder.setOnClickListener(this);
        // 会员专享打折优惠
        huiyuan_zhuanxiang = (TextView) findViewById(R.id.textView18);
        order_list.setLayoutManager(new LinearLayoutManager(this));
        OrderListAdapter orderListAdapter = new OrderListAdapter(OrderActivity.this, data, R.layout.order_list_contont);
        order_list.setAdapter(orderListAdapter);

    }

    private void initData() {

        /**
         * 获取实时打折的折扣数
         */

        userModel.getPersonCentern(new Callback<UserInfo>() {
            private String buyProportion;

            @Override
            public void onSuccess(UserInfo userInfo) {
                buyProportion = userInfo.buyProportion;
                huiyuan_zhuanxiang.setText(Integer.parseInt(buyProportion) * 0.01 + "");
                shijiprice = (int) (zongjai * Integer.parseInt(buyProportion) * 0.01);
                // 在后两位前插入小数点
                String s = shijiprice + "";
                StringBuilder sb = new StringBuilder(shijiprice + "");
                sb.insert(s.length() - 2, ".");
                String marStrNew = sb.toString();
                shiji_price.setText(marStrNew);
            }

            @Override
            public void onFailure(int resultCode, String message) {
            }
        });

        /**
         * 获取默认地址列表
         */
        userModel.getMorenAddress(new Callback<MorenAddressBody>() {
            @Override
            public void onSuccess(MorenAddressBody morenAddressBody) {
                morenAddressBody1 = morenAddressBody;
                setuserImformation(morenAddressBody1);
            }

            @Override
            public void onFailure(int resultCode, String message) {
            }
        });
    }

    private void setuserImformation(MorenAddressBody morenAddressBody) {

        moren_xuanzhong_address.setText(morenAddressBody.address + morenAddressBody.addressDetail);
        moren_name.setText(morenAddressBody.userName);
        moren_phone.setText(morenAddressBody.userMobile);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                if(zhuangtai==0){
                    mapp.setZhaungtai(0);
                }else{
                    mapp.setZhaungtai(1);
                }
                finish();
                break;
            case R.id.user_address:
                Intent intent = new Intent(OrderActivity.this, TheGoodsAddress.class);
                startActivityForResult(intent, requestCode);
                break;
            case R.id.textView15:
                popupWindow();
                break;
            case R.id.zhifubao:
                type=1;
                zhifubao_src.setImageResource(R.mipmap.xuanzhong);
                weixin_src.setImageResource(R.mipmap.weixuan);
                break;
            case R.id.weixin:
                type=2;
                zhifubao_src.setImageResource(R.mipmap.weixuan);
                weixin_src.setImageResource(R.mipmap.xuanzhong);
                break;
            case R.id.queren:
                // 提交订单
                zhuangtai=1;
                SubmitOrderBody submitOrderBody = new SubmitOrderBody();
                submitOrderBody.contactPhone = morenAddressBody1.userMobile;
                submitOrderBody.address = morenAddressBody1.address + morenAddressBody1.addressDetail;
                submitOrderBody.contactName = morenAddressBody1.userName;


                for (int i = 0; i < data.size(); i++) {

                    ProductSDeatilBody productSDeatilBody = new ProductSDeatilBody();
                    productSDeatilBody.price = data.get(i).productFormatPrice;
                    productSDeatilBody.count = data.get(i).goumaishuliang;
                    productSDeatilBody.logoUrl = data.get(i).logoUrl;
                    productSDeatilBody.productId = data.get(i).productId;
                    productSDeatilBody.productName = data.get(i).productName;
                    productSDeatilBody.formatName = data.get(i).formatName;
                    productSDeatilBody.shopCarId = data.get(i).shopCarId;
                    list.add(productSDeatilBody);
                }
                submitOrderBody.orderInfoList = list;
                userModel.getSubmitOrder(submitOrderBody, new Callback<SubmitOrderBody>() {
                    @Override
                    public void onSuccess(SubmitOrderBody submitOrderBody) {
                        String orderNo = submitOrderBody.orderNo;
                        perform(orderNo);
                    }

                    @Override
                    public void onFailure(int resultCode, String message) {
                    }
                });
                break;
        }
    }

    private void perform(String orderNo) {
        /*userModel.getSubmitOrdernumber(orderNo, new Callback<Object>() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(getBaseContext(), "支付成功", Toast.LENGTH_SHORT).show();
                mCameraDialog.cancel();
                finish();
            }

            @Override
            public void onFailure(int resultCode, String message) {

            }
        });*/
        PayOrderDto payOrderDto = new PayOrderDto();
        String ipAddress = HttpUtils.getIPAddress(getBaseContext());
        if(type==1){
            payOrderDto.paymentWay=CHANNEL_ALIPAY;
        }else{
            payOrderDto.paymentWay=CHANNEL_WECHAT;
        }
        payOrderDto.clientIp=ipAddress;
        payOrderDto.orderId=orderNo;
        userModel.getPayStringBody(payOrderDto, new Callback<PayRecBody>() {
            @Override
            public void onSuccess(PayRecBody s) {
                Gson gson = new Gson();
                String json = gson.toJson(s);
                Pingpp.createPayment(OrderActivity.this, json);
            }
            @Override
            public void onFailure(int resultCode, String message) {
            }
        });




    }

    private void popupWindow() {
        mCameraDialog = new Dialog(this, R.style.my_dialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.layout_paytype_control, null);
        root.findViewById(R.id.zhifubao).setOnClickListener(OrderActivity.this);
        root.findViewById(R.id.weixin).setOnClickListener(OrderActivity.this);
        root.findViewById(R.id.queren).setOnClickListener(OrderActivity.this);
        // 切换图片
        zhifubao_src = (ImageView) root.findViewById(R.id.zhifubao_src);
        weixin_src = (ImageView) root.findViewById(R.id.weixin_src);
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        // lp.x = 0; // 新位置X坐标
        // lp.y = -20; // 新位置Y坐标
        // lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        //lp.height = 300; // 高度
        // lp.alpha = 9f; // 透明度
        // root.measure(0, 0);
        lp.height = 600;
        // lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                showMsg(result, errorMsg, extraMsg);
            }
        }

        switch (resultCode) {
            case RESULT_OK:
                Bundle extras = data.getExtras();
                AddressDetailBody address = (AddressDetailBody) extras.getSerializable("address");
                moren_name.setText(address.userName);
                moren_phone.setText(address.userMobile);
                moren_xuanzhong_address.setText(address.address + address.addressDetail);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showMsg(String title, String msg1, String msg2) {
        String str = title;
        if (null != msg1 && msg1.length() != 0) {
            str += "\n" + msg1;
        }
        if (null != msg2 && msg2.length() != 0) {
            str += "\n" + msg2;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
        builder.setMessage(str);
        builder.setTitle("提示");
        builder.setPositiveButton("OK", null);
        builder.create().show();
        mCameraDialog.cancel();
    }

}
