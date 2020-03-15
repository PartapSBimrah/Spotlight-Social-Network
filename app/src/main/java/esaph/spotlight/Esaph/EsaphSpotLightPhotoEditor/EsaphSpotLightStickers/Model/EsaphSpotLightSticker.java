package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model;

import android.graphics.Bitmap;

import java.io.Serializable;

import esaph.spotlight.Esaph.EsaphSelectAble;

public class EsaphSpotLightSticker extends EsaphSelectAble implements Serializable
{
    private long StickerTimeCreated;
    private long UIDCreator;
    private long STICKER_ID; //SpotLightStickerId
    private long STICKER_PACK_ID; //SpotLightStickerPackId
    private String IMAGE_ID;
    private Bitmap bitmap; //Dumm aber zum View Flipper nötig.

    public EsaphSpotLightSticker(long UIDCreator, long LSID, long LSPID, String IMAGE_ID, long stickerTimeCreated)
    {
        this.StickerTimeCreated = stickerTimeCreated;
        this.UIDCreator = UIDCreator;
        this.STICKER_ID = LSID;
        this.STICKER_PACK_ID = LSPID;
        this.IMAGE_ID = IMAGE_ID;
    }

    public String getIMAGE_ID() {
        return IMAGE_ID;
    }

    public long getUIDCreator() {
        return UIDCreator;
    }

    public long getStickerTimeCreated() {
        return StickerTimeCreated;
    }

    public long getSTICKER_ID() {
        return STICKER_ID;
    }

    public long getSTICKER_PACK_ID() {
        return STICKER_PACK_ID;
    }

    public void setSTICKER_ID(long STICKER_ID) {
        this.STICKER_ID = STICKER_ID;
    }

    public void setSTICKER_PACK_ID(long STICKER_PACK_ID) {
        this.STICKER_PACK_ID = STICKER_PACK_ID;
    }
}
