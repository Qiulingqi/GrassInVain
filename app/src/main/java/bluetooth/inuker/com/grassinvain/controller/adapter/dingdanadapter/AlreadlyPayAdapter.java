package bluetooth.inuker.com.grassinvain.controller.adapter.dingdanadapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shaojun.widget.superAdapter.SuperAdapter;
import com.shaojun.widget.superAdapter.internal.SuperViewHolder;

import java.util.List;

import bluetooth.inuker.com.grassinvain.R;
import bluetooth.inuker.com.grassinvain.controller.adapter.WaitPayAdapter;
import bluetooth.inuker.com.grassinvain.network.body.response.AllOrderfirstBody;
import bluetooth.inuker.com.grassinvain.network.body.response.AllOrdersecondBody;

/**
 * Created by 1 on 2017/4/11.
 */

public class AlreadlyPayAdapter extends SuperAdapter<AllOrderfirstBody> {

    private TextView button1;
    private TextView button2;

    /**
     * Constructor for single itemView type.
     *
     * @param context
     * @param items
     * @param layoutResId
     */
    public AlreadlyPayAdapter(Context context, List<AllOrderfirstBody> items, int layoutResId) {
        super(context, items, layoutResId);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, final int layoutPosition, final AllOrderfirstBody item) {


        TextView danhao = holder.findViewById(R.id.textView22);
        danhao.setText(item.orderNo);
        TextView time = holder.findViewById(R.id.time);
        time.setText(item.createAt);
        TextView hejiprice = holder.findViewById(R.id.textView25);
        hejiprice.setText(item.amount);
        /**
         * 子布局
         */
        RecyclerView recyclerView = holder.findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false) {

            @Override
            public boolean canScrollVertically() {

                return false;

            }

        };
        recyclerView.setLayoutManager(linearLayoutManager);
        List<AllOrdersecondBody> orderInfoList = item.orderInfoList;

        WaitPayAdapter adapter = new WaitPayAdapter(getContext(), orderInfoList, R.layout.order_list_contont);

        recyclerView.setAdapter(adapter);

        /**
         * 操作按钮的事件
         */

        button1 = holder.findViewById(R.id.textView27);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != chooseType){
                    chooseType.chooseWuliu(layoutPosition,button1.getText().toString(),item.orderNo);
                }
            }
        });
        button2 = holder.findViewById(R.id.textView26);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( null != chooseType){
                    chooseType.chooseShouhuo(layoutPosition,button2.getText().toString(),item.orderNo);
                }
            }
        });
        if ("PAD" .equals(item.status)) {
            button1.setText("查看物流");
            button2.setText("确认收货");
        }
    }
    public ChooseType chooseType;

    public void setOnItemClickListener(ChooseType chooseType) {
        this.chooseType = chooseType;
    }

    public interface ChooseType {

        void chooseWuliu(int positon, String type, String order);
        void chooseShouhuo(int positon, String type, String order);
    }

}
