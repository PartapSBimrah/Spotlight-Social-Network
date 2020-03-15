package esaph.spotlight.navigation;

public interface CameraCallback
{
    void onOkayAddSendFragment(String PID, String Type);
    void onRemoveFragment();
    void onPostSend();
    void onClickArrowDragSentListUp();
}
