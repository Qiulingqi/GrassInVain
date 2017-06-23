package bluetooth.inuker.com.grassinvain.network.body.response;

import java.io.Serializable;

/**
 * Created by 1 on 2017/6/16.
 */

public class PayRecBody implements Serializable{
    public long amount;
    public long amountRefunded;
    public long amountSettle;
    public String app;
    public String body;
    public String channel;
    public String clientIp;
    public long created;
    public String currency;
    public Object extra;
    public String id;
    public boolean livemode;
    public Object metadata;
    public String object;
    public String orderNo;
    public boolean paid;
    public boolean refunded;
    public String subject;
    public long timeExpire;
}
