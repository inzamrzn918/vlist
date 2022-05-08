package in.rbofficial.barpeta.vlist.ui.single;

import android.Manifest;
import android.app.DownloadManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import in.rbofficial.barpeta.vlist.databinding.FragmentSingleBinding;
import ir.siaray.downloadmanagerplus.classes.Downloader;
import ir.siaray.downloadmanagerplus.enums.DownloadReason;
import ir.siaray.downloadmanagerplus.model.DownloadItem;

public class SingleFragment extends Fragment {
    private JSONArray dist_array;
    public static final String TAG = "NO TAG HERE";
    private List<String> distList, distCode, lacList, lacCode, year, partList, partCodeList;
    private JSONArray array;
    private ArrayAdapter<String> adapter;
    private DownloadListener listener;

    private FragmentSingleBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSingleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        distList = new ArrayList<>();
        distCode = new ArrayList<>();
        lacList = new ArrayList<>();
        lacCode = new ArrayList<>();
        partList = new ArrayList<>();
        partCodeList = new ArrayList<>();
        year = new ArrayList<>();
        year.add("2022");
        array = new JSONArray();

        getJSONArrayFromUrl("https://raw.githubusercontent.com/inzamrzn918/njs/master/data.json");
        binding.year.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, year));
        binding.district.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line,distList));
        binding.lac.setAdapter(adapter);



        return root;
    }

    private void getJSONArrayFromUrl(String url) {
        binding.pbr.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest request = new StringRequest(Request.Method.GET,url,
                response -> {
                    binding.pbr.setVisibility(View.GONE);
                    try {
                        array = new JSONArray(response);
                        for(int i =0; i<array.length(); i++){
//                            Log.d(TAG, "getJSONArrayFromUrl: "+array.get(i));
                            JSONObject district = array.getJSONObject(i);
//
                            String distname = district.getString("dist_name");
                            distList.add(distname);
                            distCode.add(district.getString("dist_code"));

                        }
                        binding.district.setOnItemClickListener((adapterView, view, i, l) -> {
                            lacList.clear();
                            String dn = binding.district.getText().toString();
                            int index = -2;
                            for(int j =0; j<array.length(); j++){
                                try {
                                    JSONObject district = array.getJSONObject(i);
                                    String distname = district.getString("dist_name");
                                    if(distname.equals(dn)){
                                        index = i;
                                    }else {
                                        index = -2;
                                    }
                                    Log.d(TAG, "getJSONArrayFromUrl: "+distname);
                                    distList.add(distname);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            if(index!=-2){
                                try {
                                    JSONObject object = array.getJSONObject(index);
                                    Log.d(TAG, "getJSONArrayFromUrl: "+object);
                                    JSONArray lacs = object.getJSONArray(object.getString("dist_name"));
                                    for(int ii=0; ii< lacs.length(); ii++){
                                        JSONObject lac = lacs.getJSONObject(ii);
                                        lacList.add(lac.getString("lac_name").trim());
                                        lacCode.add(lac.getString("lac_no"));
                                    }
                                    adapter = new ArrayAdapter<>(
                                            requireContext(),
                                            android.R.layout.simple_dropdown_item_1line,
                                            lacList
                                    );
                                    binding.lac.setAdapter(adapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        });
//
                        binding.lac.setOnItemClickListener((adapterView, view, i, l) -> {
                            partList.clear();
                            partCodeList.clear();
                            String ln = binding.lac.getText().toString();
                            String dn = binding.district.getText().toString();
                            int index = -2;
                            int pindex=-2;
                            AtomicInteger pdfIndex= new AtomicInteger(-2);
                            for(int j =0; j<array.length(); j++){
                                try {
                                    JSONObject district = array.getJSONObject(j);
                                    String disName = district.getString("dist_name");
                                    if(disName.equals(dn)){
                                        Log.d(TAG, "getJSONArrayFromUrl: DN "+dn);
                                        index = j;
                                        JSONArray lacs = district.getJSONArray(district.getString("dist_name"));
                                        for(int ii=0; ii<lacs.length(); ii++){
                                            Log.d(TAG, "getJSONArrayFromUrl: LN "+ln);
                                            JSONObject lacObj = lacs.getJSONObject(ii);
                                            String lacName = lacObj.getString("lac_name");
                                            if(lacName.trim().equals(ln.trim())){
                                                Log.d(TAG, "getJSONArrayFromUrl: LAC_NAME "+lacObj.getJSONArray(lacObj.getString("lac_name")));
                                                pindex=ii;

                                                break;
                                            }
                                        }
                                        break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            if(index!=-2 && pindex!=-2){

                                try {
                                    JSONObject object = array.getJSONObject(index);
                                    JSONArray lac_array = object.getJSONArray(object.getString("dist_name"));
                                    JSONObject partObj = lac_array.getJSONObject(pindex);
                                    JSONArray parts = partObj.getJSONArray(partObj.getString("lac_name"));
                                    for (int j=0 ; j<parts.length(); j++){
                                        String scnmae = parts.getJSONObject(j).getString("school_name");
                                        partList.add(scnmae);
                                        partCodeList.add(scnmae);

                                    }
                                    adapter = new ArrayAdapter<>(
                                            requireContext(),
                                            android.R.layout.simple_dropdown_item_1line,
                                            partList
                                    );
                                    binding.vcenter.setAdapter(adapter);
                                    binding.vcenter.setOnItemClickListener((adapterView1, view12, i1, l1) -> {
                                        binding.btnProgress.setVisibility(View.VISIBLE);
                                        binding.btnProgress.setOnClickListener(view13 -> {
                                            grantPermission();
                                            for (int p = 0; p <parts.length(); p++) {
                                                try {
                                                    if(binding.vcenter.getText().toString().trim()
                                                            .equals(parts.getJSONObject(p).getString("school_name").trim())){
                                                        pdfIndex.set(p);
                                                        break;
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            Log.d(TAG, "getJSONArrayFromUrl: "+pdfIndex.get());
                                            if(pdfIndex.get() !=-2){
                                                try {
                                                    JSONObject urlObject = parts.getJSONObject(pdfIndex.get());
                                                    String pdfUrl = urlObject.getString("url");
                                                    String path ="VOTER LIST/"+ object.getString("dist_name").trim()+"/"+
                                                            partObj.getString("lac_name").trim()+"/"
                                                            +urlObject.getString("school_name").trim().replace('/','_')+".pdf";
                                                    File file = new File(Environment.DIRECTORY_DOCUMENTS,path);
                                                    if(file.exists()){
                                                        Toast.makeText(requireContext(),
                                                                "The list for this school is already downloaded",Toast.LENGTH_LONG).show();
                                                    }else {
                                                        Downloader downloader = Downloader.getInstance(requireContext())
                                                                .setUrl(pdfUrl)
                                                                .setListener(new ir.siaray.downloadmanagerplus.interfaces.DownloadListener() {
                                                                    @Override
                                                                    public void onComplete(int totalBytes, DownloadItem downloadInfo) {
                                                                        Toast.makeText(requireContext(),"Download Completed!", Toast.LENGTH_LONG).show();
                                                                        binding.btnProgress.setVisibility(View.GONE);
                                                                    }

                                                                    @Override
                                                                    public void onPause(int percent, DownloadReason reason, int totalBytes, int downloadedBytes, DownloadItem downloadInfo) {
                                                                        Toast.makeText(requireContext(),"Download Paused!", Toast.LENGTH_LONG).show();
                                                                    }

                                                                    @Override
                                                                    public void onPending(int percent, int totalBytes, int downloadedBytes, DownloadItem downloadInfo) {
                                                                        Toast.makeText(requireContext(),"Download pending at "+percent+"%", Toast.LENGTH_LONG).show();
                                                                    }

                                                                    @Override
                                                                    public void onFail(int percent, DownloadReason reason, int totalBytes, int downloadedBytes, DownloadItem downloadInfo) {
                                                                        Toast.makeText(requireContext(),"Failed ! "+response, Toast.LENGTH_LONG).show();
                                                                    }

                                                                    @Override
                                                                    public void onCancel(int totalBytes, int downloadedBytes, DownloadItem downloadInfo) {
                                                                        Toast.makeText(requireContext(),"You have Canceled!", Toast.LENGTH_LONG).show();
                                                                    }

                                                                    @Override
                                                                    public void onRunning(int percent, int totalBytes, int downloadedBytes, float downloadSpeed, DownloadItem downloadInfo) {
                                                                        Toast.makeText(requireContext(),"Download Complete! "+percent+"%", Toast.LENGTH_LONG).show();
                                                                    }
                                                                })
                                                                .setToken(Downloader.getToken(requireContext(),
                                                                        Long.parseLong(urlObject.getString("part_no"))))
                                                                .setAllowedOverRoaming(true)
                                                                .setAllowedOverMetered(true) //Api 16 and higher
                                                                .setVisibleInDownloadsUi(true)
                                                                .setDestinationDir(Environment.DIRECTORY_DOCUMENTS,path)
                                                                .setNotificationTitle(urlObject.getString("school_name").trim().replace('/','_')+".pdf")
                                                                .setDescription("2022 - " + " - " + object.getString("dist_name").trim() + " - " + partObj.getString("lac_name").trim())
                                                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                                                .setKeptAllDownload(false);

                                                        downloader.start();
                                                    }



                                                    Toast.makeText(requireContext(),"Download Started",Toast.LENGTH_LONG).show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }


                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            binding.pbr.setVisibility(View.GONE);
            array = new JSONArray();
        });
        queue.add(request);

    }

    private void grantPermission() {
        Dexter.withContext(requireContext())
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if(multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            Toast.makeText(requireContext(), "Ops ! you need to allow the permissions",
                                    Toast.LENGTH_LONG).show();
                            grantPermission();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}