package bluetooth.inuker.com.grassinvain.controller.fragment.aboutproduct;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pingplusplus.android.Pingpp;

import java.util.ArrayList;
import java.util.List;

import bluetooth.inuker.com.grassinvain.R;
import bluetooth.inuker.com.grassinvain.controller.adapter.AlloederFragmentAdapter;
import bluetooth.inuker.com.grassinvain.network.body.HttpUtils;
import bluetooth.inuker.com.grassinvain.network.body.request.PayOrderDto;
import bluetooth.inuker.com.grassinvain.network.body.response.AllOrderBody;
import bluetooth.inuker.com.grassinvain.network.body.response.AllOrderfirstBody;
import bluetooth.inuker.com.grassinvain.network.body.response.PageBody;
import bluetooth.inuker.com.grassinvain.network.body.response.PayRecBody;
import bluetooth.inuker.com.grassinvain.network.model.UserModel;
import bluetooth.inuker.com.grassinvain.network.model.callback.Callback;

/**
 * Created by 1 on 2017/4/19.
 */

public class AlloederFragment extends Fragment implements View.OnClickListener{

    private View view;
    private UserModel userModel;
    private SwipeRefreshLayout swipeRefreshAllOrder;
    private RecyclerView allorderrecycView;
    private List<AllOrderfirstBody> data = new ArrayList<>();
    private AlloederFragmentAdapter alloederFragmentAdapter;
    private int pagesize = 1;
    private Dialog mCameraDialog;
    private ImageView zhifubao_src;
    private ImageView weixin_src;
    private int type=1;
    private String orderNo;

    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.all_order_fragment_activity, null);
        userModel = new UserModel(getActivity());
        initData(pagesize);
        initView();
        return view;
    }

    private void initView() {

        // 原生刷新，控件
        swipeRefreshAllOrder = (SwipeRefreshLayout) view.findViewById(R.id.SwipeRefresh_all_order);
        //  设置加载是的属性
        swipeRefreshAllOrder.setColorSchemeResources(R.color.zhuticolor, R.color.color_red_pay);
        swipeRefreshAllOrder.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                data.clear();
                alloederFragmentAdapter.clear();
                pagesize=1;
                initData(pagesize);
                swipeRefreshAllOrder.setRefreshing(false);
                Toast.makeText(getActivity(), "刷新完毕", Toast.LENGTH_SHORT).show();
            }
        });
        allorderrecycView = (RecyclerView) view.findViewById(R.id.allorderrecycView);
        allorderrecycView.setLayoutManager(new LinearLayoutManager(getActivity()));
        alloederFragmentAdapter = new AlloederFragmentAdapter(getActivity(), data, R.layout.wait_pay_contont);
        allorderrecycView.setAdapter(alloederFragmentAdapter);

        /**
         * 实现下拉加载
         */
        allorderrecycView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == alloederFragmentAdapter.getItemCount()) {
                    Toast.makeText(getActivity(), "正在加载........", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pagesize++;
                            initData(pagesize);
                        }
                    }, 500);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
        /**
         * 判断按钮的点击事件
         */

        alloederFragmentAdapter.setOnItemClickListener(new AlloederFragmentAdapter.ChooseType() {
            @Override
            public void choosebutton1(int positon, String type, String order) {
                if ("去评价".equals(type)) {
                    Toast.makeText(getActivity(), "去评价", Toast.LENGTH_SHORT).show();
                    /*Intent intent1 = new Intent(getActivity(), EvaluationGoods.class);
                    intent1.putExtra("order", order);
                    startActivity(intent1);*/
                }
                if ("查看物流".equals(type)) {
                    Toast.makeText(getActivity(), "查看物流", Toast.LENGTH_SHORT).show();
                  /*  Intent intent1 = new Intent(getActivity(), WuliuActivity.class);
                    intent1.putExtra("order", order);
                    startActivity(intent1);*/
                }
                if ("取消订单".equals(type)) {
                    Toast.makeText(getActivity(), "取消订单", Toast.LENGTH_SHORT).show();
                    userModel.getCancelOrder(order, new Callback<Object>() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(getActivity(), "取消成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int resultCode, String message) {

                        }
                    });
                }
            }

            @Override
            public void choosebutton2(int positon, String type, String order) {

                if ("去付款".equals(type)) {
                    //Toast.makeText(getActivity(), "去付款", Toast.LENGTH_SHORT).show();
                    orderNo=order;
                    popupWindow();
                }
                if ("确认收货".equals(type)) {
                    Toast.makeText(getActivity(), "确认收货", Toast.LENGTH_SHORT).show();
                    userModel.getQuerenOrder(order, new Callback<Object>() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(getActivity(), "已确认收货", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(int resultCode, String message) {
                        }
                    });
                }
                if ("删除订单".equals(type)) {
                    Toast.makeText(getActivity(), "删除订单", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void initData(int pagesize) {
        PageBody pageBody = new PageBody();
        pageBody.pageNum = pagesize;
        pageBody.pageSize = 10;
        pageBody.status = "";
        userModel.getAllOrder(pageBody, new Callback<AllOrderBody>() {
            @Override
            public void onSuccess(AllOrderBody allOrderBody) {
                List<AllOrderfirstBody> list = allOrderBody.list;
                if (0 == list.size()) {
                    Toast.makeText(getActivity(), "数据已全部加载完毕", Toast.LENGTH_SHORT).show();
                }else{
                    data.addAll(list);
                    alloederFragmentAdapter.addAll(data);
                    alloederFragmentAdapter.notifyDataSetHasChanged();
                }

            }

            @Override
            public void onFailure(int resultCode, String message) {

            }
        });
    }


    private void popupWindow() {
        mCameraDialog = new Dialog(getActivity(), R.style.my_dialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(getActivity()).inflate(
                R.layout.layout_paytype_control, null);
        root.findViewById(R.id.zhifubao).setOnClickListener(this);
        root.findViewById(R.id.weixin).setOnClickListener(this);
        root.findViewById(R.id.queren).setOnClickListener(this);
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
    public void onClick(View v) {
        switch (view.getId()) {
            case R.id.zhifubao:
                zhifubao_src.setImageResource(R.mipmap.xuanzhong);
                weixin_src.setImageResource(R.mipmap.weixuan);
                type=1;
                break;
            case R.id.weixin:
                zhifubao_src.setImageResource(R.mipmap.weixuan);
                weixin_src.setImageResource(R.mipmap.xuanzhong);
                type=2;
                break;
            case R.id.queren:
                PayOrderDto payOrderDto = new PayOrderDto();
                String ipAddress = HttpUtils.getIPAddress(getActivity());
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
                        Pingpp.createPayment(getActivity(), json);
                    }
                    @Override
                    public void onFailure(int resultCode, String message) {
                    }
                });
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(str);
        builder.setTitle("提示");
        builder.setPositiveButton("OK", null);
        builder.create().show();
        mCameraDialog.cancel();
    }

}
