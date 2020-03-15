/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphGlobalImageLoader.RequestBuilder;

import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.ReentrantLock;

import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphDimension;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.EsaphGlobalImageLoader;
import esaph.spotlight.Esaph.EsaphGlobalImageLoader.imageaware.ImageViewAware;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;

public class CanvasRequest extends BaseRequest
{
    public final ImageViewAware imageViewAware;
    public final ConversationMessage conversationMessage;
    public final WeakReference<EsaphGlobalImageLoader.ResourceReadyListener> resourceReadyListenerWeakReference;
    public final boolean autoTextSize = false;

    private CanvasRequest(ReentrantLock reentrantLock,
                          ImageViewAware imageViewAware,
                          EsaphDimension esaphDimension,
                          ConversationMessage conversationMessage,
                          EsaphGlobalImageLoader.ResourceReadyListener resourceReadyListener)
    {
        super(reentrantLock,
                imageViewAware,
                StorageHandler.FOLDER__SPOTLIGHT_MESSAGES,
                Long.toString(conversationMessage.getMESSAGE_ID()),
                StorageHandler.SPOTMESSAGE_IMAGE_PREFIX,
                null,
                null);

        this.resourceReadyListenerWeakReference = new WeakReference<>(resourceReadyListener);
        this.imageViewAware = imageViewAware;
        this.conversationMessage = conversationMessage;
    }

    public static CanvasRequestBuilder builder(ImageView imageView, EsaphDimension esaphDimension, ConversationMessage conversationMessage)
    {
        return new CanvasRequestBuilder(new ImageViewAware(imageView),
                esaphDimension,
                conversationMessage);
    }

    public static class CanvasRequestBuilder
    {
        private ImageViewAware imageViewAware;
        private EsaphDimension esaphDimension;
        private ConversationMessage conversationMessage;
        private EsaphGlobalImageLoader.ResourceReadyListener resourceReadyListener;
        private boolean autoTextSize = false;

        public CanvasRequestBuilder(ImageViewAware imageViewAware, EsaphDimension esaphDimension, ConversationMessage conversationMessage)
        {
            this.imageViewAware = imageViewAware;
            this.esaphDimension = esaphDimension;
            this.conversationMessage = conversationMessage;
        }

        public CanvasRequestBuilder on(ImageViewAware imageViewAware, EsaphDimension esaphDimension, ConversationMessage conversationMessage)
        {
            return new CanvasRequestBuilder(imageViewAware, esaphDimension, conversationMessage);
        }

        public void setResourceReadyListener(EsaphGlobalImageLoader.ResourceReadyListener resourceReadyListener) {
            this.resourceReadyListener = resourceReadyListener;
        }

        public CanvasRequestBuilder setAutoTextSize(boolean autoTextSize)
        {
            this.autoTextSize = autoTextSize;
            return this;
        }

        public CanvasRequest build(ReentrantLock reentrantLock)
        {
            return new CanvasRequest(reentrantLock,
                    imageViewAware,
                    esaphDimension,
                    conversationMessage,
                    resourceReadyListener);
        }

        public ConversationMessage getConversationMessage() {
            return conversationMessage;
        }
    }
}
