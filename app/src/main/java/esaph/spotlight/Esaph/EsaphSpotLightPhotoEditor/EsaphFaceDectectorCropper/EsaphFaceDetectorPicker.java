package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.Adapter.DetectedFacesAdapter;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.AddStickerToPackManaging.AsyncDeleteSticker;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.AddStickerToPackManaging.AsyncSaveSticker;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.AddStickerToPackManaging.DialogAddStickerToPack;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightStickerPack;
import esaph.spotlight.R;

public class EsaphFaceDetectorPicker extends BottomSheetDialogFragment
{
    private List<EsaphSpotLightSticker> esaphSpotLightStickerFacesDetected;
    private DetectedFacesAdapter detectedFacesAdapter;
    private RecyclerView recyclerView;

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        textViewConfirmAddingDialog = null;
        dialogAddStickerToPack = null;
        esaphSpotLightStickerFacesDetected = null;
    }

    public EsaphFaceDetectorPicker()
    {
        // Required empty public constructor
    }

    private static final String extraInterface = "esaph.spotlight.interface.esaphfacedetectorpicker";

    public static EsaphFaceDetectorPicker getInstance(FaceDetectorPickerDataTransferInterface faceDetectorPickerDataTransferInterface)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EsaphFaceDetectorPicker.extraInterface, faceDetectorPickerDataTransferInterface);
        EsaphFaceDetectorPicker esaphFaceDetectorPicker = new EsaphFaceDetectorPicker();
        esaphFaceDetectorPicker.setArguments(bundle);
        return esaphFaceDetectorPicker;
    }

    public interface FaceDetectorPickerDataTransferInterface extends Serializable
    {
        List<EsaphSpotLightSticker> onTransferList();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            FaceDetectorPickerDataTransferInterface dataTransferInterface =
                    (FaceDetectorPickerDataTransferInterface) bundle.getSerializable(EsaphFaceDetectorPicker.extraInterface);
            if(dataTransferInterface != null)
            {
                esaphSpotLightStickerFacesDetected = dataTransferInterface.onTransferList();
            }
            else
            {
                esaphSpotLightStickerFacesDetected = new ArrayList<>();
            }
        }

        detectedFacesAdapter = new DetectedFacesAdapter(this,
                getContext(), esaphSpotLightStickerFacesDetected);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_esaph_face_detector_picker, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(detectedFacesAdapter);
    }

    private DialogAddStickerToPack dialogAddStickerToPack;

    private TextView textViewConfirmAddingDialog;
    public void handleStickerAddClick(final EsaphSpotLightSticker esaphSpotLightSticker)
    {
        if(esaphSpotLightSticker.isSelected())
        {
            new AsyncDeleteSticker(getContext(),
                    esaphSpotLightSticker,
                    new AsyncDeleteSticker.StickerDeletingListener()
                    {
                        @Override
                        public void onStickerUpdate(EsaphSpotLightSticker esaphSpotLightSticker)
                        {
                            if(isAdded())
                            {
                                detectedFacesAdapter.updateItem(esaphSpotLightSticker);
                            }
                        }
                    }).execute();
        }
        else
        {
            Activity activity = getActivity();
            if(activity != null)
            {
                dialogAddStickerToPack = new DialogAddStickerToPack(activity,
                        esaphSpotLightSticker,
                        new DialogAddStickerToPack.ItemSelectListener()
                        {
                            @Override
                            public void onSelectionChanged(int totalCount)
                            {
                                if(textViewConfirmAddingDialog != null)
                                {
                                    if(totalCount > 0)
                                    {
                                        textViewConfirmAddingDialog.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        textViewConfirmAddingDialog.setVisibility(View.GONE);
                                    }
                                }
                            }
                        });

                dialogAddStickerToPack.show();

                textViewConfirmAddingDialog = (TextView) dialogAddStickerToPack.findViewById(R.id.textViewAddStickerConfirm);
                textViewConfirmAddingDialog.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        List<EsaphSpotLightStickerPack> listSelected = dialogAddStickerToPack.getAdapterShowStickerPacks().getSelectedItems();
                        new AsyncSaveSticker(getActivity(),
                                listSelected,
                                esaphSpotLightSticker,
                                new AsyncSaveSticker.StickerSavingListener()
                                {
                                    @Override
                                    public void onStickerUpdate(EsaphSpotLightSticker esaphSpotLightSticker)
                                    {
                                        if(isAdded())
                                        {
                                            detectedFacesAdapter.updateItem(esaphSpotLightSticker);
                                        }
                                    }
                                }).execute();
                        dialogAddStickerToPack.dismiss();
                        dialogAddStickerToPack = null;
                    }
                });
            }
        }
    }
}
