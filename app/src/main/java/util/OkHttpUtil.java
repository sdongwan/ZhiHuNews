package util;



import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2016/11/20.
 */

public class OkHttpUtil {

    public static OkHttpUtil pOkHttpUtil;

    private static OkHttpClient sOkHttpClient;

    private OkHttpUtil() {

    }

    public static OkHttpUtil getInstance() {
        if (pOkHttpUtil == null) {
            init();
            return new OkHttpUtil();
        }
        return pOkHttpUtil;
    }

    private static void init() {
        sOkHttpClient = new OkHttpClient();
    }

    public void getFromNet(String url, Callback callback) {
        Request request = new Request.Builder().url(url).get().build();
        Call call = sOkHttpClient.newCall(request);
        call.enqueue(callback);
    }


}
