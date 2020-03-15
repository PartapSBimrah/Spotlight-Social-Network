package esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.LifeCloudBigViewMaster;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.view.ViewGroup;

import java.util.List;

import esaph.spotlight.Esaph.EsaphLifeCloudBackup.DisplayingFragment.PictureFragmentLifeCloud;
import esaph.spotlight.Esaph.EsaphLifeCloudBackup.DisplayingFragment.VideoFragmentLifeCloud;
import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;
import esaph.spotlight.navigation.globalActions.CMTypes;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.ViewPagerAdapterGetList;
import esaph.spotlight.navigation.spotlight.Moments.AktuellePrivateMomentView.DatumHolderFragment;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.YearTime;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.DatumList;

public class LifeCloudBigViewAdapter extends ViewPagerAdapterGetList
{
    private ViewPager.OnPageChangeListener mainPagerListener;
    private ViewPager viewPager;
    private Fragment currentFragment = null;
    private Context context;
    private List<Object> list;
    private boolean ignoreFirst = true;

    public LifeCloudBigViewAdapter(FragmentManager manager, Context context, List<Object> list,
                                   ViewPager.OnPageChangeListener mainPagerListener,
                                   ViewPager viewPager)
    {
        super(manager);
        this.list = list;
        this.context = context;
        this.mainPagerListener = mainPagerListener;
        this.viewPager = viewPager;
    }

    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
        if(!ignoreFirst)
        {
            mainPagerListener.onPageSelected(viewPager.getCurrentItem());
        }
        ignoreFirst = false;
    }

    public void updatePostByPid(LifeCloudUpload lifeCloudUpload)
    {
        for(int counter = 0; counter < list.size(); counter++)
        {
            System.out.println("DEBUG LIFECLOUD: Searching");
            Object object = list.get(counter);
            if(object instanceof LifeCloudUpload)
            {
                LifeCloudUpload lifeCloudUploadIntern = (LifeCloudUpload) object;
                if(lifeCloudUpload.getCLOUD_PID().equals(lifeCloudUploadIntern.getCLOUD_PID()))
                {
                    System.out.println("DEBUG LIFECLOUD: Found");
                    list.set(counter, lifeCloudUpload);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void updatePostByPid(LifeCloudUpload lifeCloudUpload, LifeCloudUpload lifeCloudUploadReplace)
    {
        for(int counter = 0; counter < list.size(); counter++)
        {
            Object object = list.get(counter);
            if(object instanceof LifeCloudUpload)
            {
                LifeCloudUpload lifeCloudUploadIntern = (LifeCloudUpload) object;
                if(lifeCloudUpload.getCLOUD_PID().equals(lifeCloudUploadIntern.getCLOUD_PID()))
                {
                    list.set(counter, lifeCloudUploadReplace);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public List<Object> getList()
    {
        return list;
    }

    @Override
    public int[] getObjectsCount() {
        return new int[0];
    }

    public Fragment getCurrentFragment()
    {
        return currentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object)
    {
        if (getCurrentFragment() != object)
        {
            currentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    public void removePostByPid(String PID)
    {
        for(int counter = 0; counter < list.size(); counter++)
        {
            Object object = list.get(counter);
            if(object instanceof LifeCloudUpload)
            {
                if(((LifeCloudUpload)object).getCLOUD_PID().equals(PID))
                {
                    if(counter - 1 > -1 && (list.get(counter-1) instanceof DatumList || list.get(counter-1) instanceof YearTime))
                    {
                        list.remove(counter-1);
                        list.remove(counter-1);
                    }
                    else
                    {
                        list.remove(counter);
                    }
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public Fragment getItem(int position)
    {
        Object object = getItemFromList(position);

        if(object instanceof LifeCloudUpload)
        {
            LifeCloudUpload lifeCloudUpload = (LifeCloudUpload) object;
            if(lifeCloudUpload.getCLOUD_POST_TYPE() == (CMTypes.FPIC))
            {
                return PictureFragmentLifeCloud.getInstance(lifeCloudUpload.getCLOUD_PID());
            }
            else if(lifeCloudUpload.getCLOUD_POST_TYPE() == (CMTypes.FVID))
            {
                return VideoFragmentLifeCloud.getInstance(lifeCloudUpload.getCLOUD_PID());
            }
        }
        else if(object instanceof DatumList)
        {
            DatumList datumHolder = (DatumList) object;
            return DatumHolderFragment.getInstance(datumHolder);
        }
        return null;
    }

    public Object getItemFromList(int position)
    {
        return list.get(position);
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object)
    {
        return POSITION_NONE;
    }
}
