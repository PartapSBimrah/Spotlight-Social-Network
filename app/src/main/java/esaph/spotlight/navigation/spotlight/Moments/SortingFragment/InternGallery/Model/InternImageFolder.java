package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.InternGallery.Model;

import java.util.ArrayList;

public class InternImageFolder
{
    private String FolderName;
    private ArrayList<String> arrayListAllImagesPath;

    public InternImageFolder(String FolderName, ArrayList<String> arrayListAllImagesPath)
    {
        this.FolderName = FolderName;
        this.arrayListAllImagesPath = arrayListAllImagesPath;
    }

    public void setAllImagesPath(ArrayList<String> arrayListAllImagesPath) {
        this.arrayListAllImagesPath = arrayListAllImagesPath;
    }

    public ArrayList<String> getArrayListAllImagesPath() {
        return arrayListAllImagesPath;
    }

    public String getFolderName() {
        return FolderName;
    }
}
