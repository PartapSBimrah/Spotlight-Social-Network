package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.SpotLightTemplates.ChatTemplatesView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder.CanvasRequest;
import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;

public class ChatTemplatePageViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private LayoutInflater inflater;
    private List<ChatTextMessage> listDisplay;
    private Context context;
    private ChatTemplatePageItemBase chatTemplatePageItemBase;


    public ChatTemplatePageViewAdapter(ChatTemplatePageItemBase chatTemplatePageItemBase,
                                         List<ChatTextMessage> chatTextMessages)
    {
        this.context = chatTemplatePageItemBase.getContext();
        this.chatTemplatePageItemBase = chatTemplatePageItemBase;
        this.listDisplay = chatTextMessages;
        this.inflater = LayoutInflater.from(context);
    }

    private static class ViewHolderTemplateItemPreview extends RecyclerView.ViewHolder
    {
        private ImageView imageViewTemplatePreview;

        public ViewHolderTemplateItemPreview(View view)
        {
            super(view);
            this.imageViewTemplatePreview = (ImageView) view.findViewById(R.id.imageViewSticker);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        View viewPost = inflater.inflate(R.layout.layout_template_spot_preview_listview_item, parent, false);
        viewHolder = new ViewHolderTemplateItemPreview(viewPost);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        final ChatTextMessage chatTextMessage = listDisplay.get(position);
        final ViewHolderTemplateItemPreview viewHolderSmiley = (ViewHolderTemplateItemPreview) holder;

        viewHolderSmiley.imageViewTemplatePreview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bitmap bm = null;
                try
                {
                    bm = ((BitmapDrawable) viewHolderSmiley.imageViewTemplatePreview.getDrawable()).getBitmap();
                }
                catch (Exception ec)
                {
                }

                if(bm != null)
                {
                    ChatTemplatesView.TemplateChatSelectedListener templateChatSelectedListener = chatTemplatePageItemBase.getTemplateChatSelectedListener();
                    if(templateChatSelectedListener != null)
                    {
                        templateChatSelectedListener.onTemplateSelected(chatTextMessage);
                    }
                }
            }
        });

        viewHolderSmiley.imageViewTemplatePreview.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                ChatTemplatesView.TemplateChatSelectedListener templateChatSelectedListener = chatTemplatePageItemBase.getTemplateChatSelectedListener();
                if(templateChatSelectedListener != null)
                {
                    templateChatSelectedListener.onTemplateLongClick(chatTextMessage); //shouldnt be there, function will get deprecated.
                }
                return true;
            }
        });

        EsaphGlobalImageLoader.with(context).canvasMode(CanvasRequest.builder(viewHolderSmiley.imageViewTemplatePreview,
                        new EsaphDimension(viewHolderSmiley.imageViewTemplatePreview.getWidth(),
                                viewHolderSmiley.imageViewTemplatePreview.getHeight()), chatTextMessage).setAutoTextSize(true));
    }

    @Override
    public int getItemCount()
    {
        return listDisplay.size();
    }
}
