package constants;

/**
 * Created by Administrator on 2016/11/20.
 */

public class ApiConstant {
    //获取最新新闻
    public final static String NEWS_LASTNEST_UTL = "http://news-at.zhihu.com/api/4/news/latest";

    //新闻详细内容，后面加上id
    public final static String NEWS_DETAIL_URL = "http://news-at.zhihu.com/api/4/news/";


    //查看以往新闻，后面加上日期
    public final static String NEWS_BEFORE_URL = "http://news-at.zhihu.com/api/4/news/before/";


    // http://news-at.zhihu.com/api/4/start-image/1080*1776
    public final static String APP_WEL_IMG = " http://news-at.zhihu.com/api/4/start-image/720*1080";

    //http://news-at.zhihu.com/api/4/story-extra/#{id} 获取新闻额外信息
    public final static String NEWS_EXTRA_INFO = "http://news-at.zhihu.com/api/4/story-extra/";

    //http://news-at.zhihu.com/api/4/story/  获取新闻长评论 后加 id/long-comments
    public final static String NEWS_LONG_COMMENT = "http://news-at.zhihu.com/api/4/story/";


    //http://news-at.zhihu.com/api/4/story/ 获取新闻段评论  后面加id/short-comments
    public final static String NEWS_SHORT_COMMENT = "http://news-at.zhihu.com/api/4/story/";

    //http://news-at.zhihu.com/api/4/themes  主题日报列表查看
    public final static String THEME_LIST_URL = "http://news-at.zhihu.com/api/4/themes";


    //http://news-at.zhihu.com/api/4/theme/  主题日报内容查看  后面加 id
    public final static String THEME_DETAIL_URL = "http://news-at.zhihu.com/api/4/theme/";

    //http://news-at.zhihu.com/api/4/theme/  {theme_id}/before/{story_id}   获取某个主题日报的加载更多
    public final static String THEME_LOAD_MORE_URL = "http://news-at.zhihu.com/api/4/theme/";

    //http://news-at.zhihu.com/api/4/editor/        查看Editor主页 后面加上  #{id}/profile-page/android
    public final static String EDITOR_HOME_PAGE = "http://news-at.zhihu.com/api/4/editor/";


}
