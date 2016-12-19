package callback;

import java.util.List;

import bean.StoriesBean;

/**
 * Created by Administrator on 2016/11/21.
 */

public interface ILoadCallBack {
    public void loadNews(List<StoriesBean> storiesBeanList);
}
