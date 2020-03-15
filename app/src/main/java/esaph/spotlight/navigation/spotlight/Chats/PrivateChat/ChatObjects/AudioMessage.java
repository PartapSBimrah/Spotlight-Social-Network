package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects;

import org.json.JSONObject;

import java.io.FileDescriptor;

import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;

public class AudioMessage extends ConversationMessage
{
    private FileDescriptor fileDescriptor;
    private long lengthMillis;
    private String AID;

    public AudioMessage(long _ID, long ABS_ID, long ID_CHAT, long uhrzeit, short messageStatus, String AID, String Absender,
                        String esaphPloppInformations)
    {
        super(_ID, ABS_ID, ID_CHAT, uhrzeit, messageStatus, CMTypes.FAUD, Absender, esaphPloppInformations);
        this.AID = AID;
    }

    public String getAID() {
        return AID;
    }

    public void setAID(String AID) {
        this.AID = AID;
    }

    public void setFileDescriptor(FileDescriptor fileDescriptor)
    {
        this.fileDescriptor = fileDescriptor;
    }

    public FileDescriptor getFileDescriptor()
    {
        return fileDescriptor;
    }

    public void setLengthMillis(long lengthMillis) {
        this.lengthMillis = lengthMillis;
    }

    public long getLengthMillis() {
        return lengthMillis;
    }
}
