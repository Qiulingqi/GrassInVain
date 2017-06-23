package bluetooth.inuker.com.grassinvain.network.body.request;

import java.io.Serializable;

/**
 * Created by 1 on 2017/6/16.
 */

public class PayOrderDto implements Serializable{
    public String orderId;
    public String paymentWay;
    public String clientIp;
}
