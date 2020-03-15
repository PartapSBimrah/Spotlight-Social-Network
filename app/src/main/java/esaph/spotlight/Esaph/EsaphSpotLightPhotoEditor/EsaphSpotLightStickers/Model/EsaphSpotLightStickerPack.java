package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model;

import java.io.Serializable;
import java.util.List;

public class EsaphSpotLightStickerPack implements Serializable
{
    public static final int VIEWTYPE_STICKER_NORMAL = 0;
    public static final int VIEWTYPE_STICKER_NEW = 1;

    private String PACK_NAME;
    private long LSPID; //SpotLightStickerPackId
    private long UIDCreator;
    private long timeCreated;
    private List<EsaphSpotLightSticker> esaphSpotLightStickers;
    private int viewType = VIEWTYPE_STICKER_NORMAL; //For recyclerView to check which viewtype.

    public EsaphSpotLightStickerPack(String PACK_NAME, long LSPID, long creator, long timeCreated, List<EsaphSpotLightSticker> esaphSpotLightStickers) {
        this.PACK_NAME = PACK_NAME;
        this.LSPID = LSPID;
        this.UIDCreator = creator;
        this.timeCreated = timeCreated;
        this.esaphSpotLightStickers = esaphSpotLightStickers;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public int getViewType() {
        return viewType;
    }

    public long getUIDCreator() {
        return UIDCreator;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public String getPACK_NAME() {
        return PACK_NAME;
    }

    public long getLSPID() {
        return LSPID;
    }

    public List<EsaphSpotLightSticker> getEsaphSpotLightStickers() {
        return esaphSpotLightStickers;
    }
}
