package pro.upchain.wallet.ui.entity;


public interface ItemClickListener
{
    void onItemClick(String url);
    default void onItemLongClick(String url) { }  //only override this if extra handling is needed
}
