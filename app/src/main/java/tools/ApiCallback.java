package tools;

public interface ApiCallback {
    void onSuccess(String loc);
    void onNewsSuccess(String[] news);
    void onError(String error);
}
