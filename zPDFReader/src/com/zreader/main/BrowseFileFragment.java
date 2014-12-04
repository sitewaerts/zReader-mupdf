package com.zreader.main;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.androidquery.AQuery;
import com.artifex.mupdfdemo.MuPDFActivity;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.zreader.database.DBRecentBooks;
import com.zreader.utils.FileManagerMenu;
import com.zreader.utils.PreferencesReader;
import com.zreader.utils.ZReaderUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BrowseFileFragment extends Fragment implements OnItemClickListener, OnBackPressedListener {
	private String root;
	private TextView myPath;
	private List<String> path = null;
	private List<BrowseItem> itemlist = null;
	private View view;
	private FragmentActivity activity;
	private String currentPath = "";
	private ItemAdapter adapter;
	private String mPath;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Bundle args = getArguments();
		String title = args.getString("title");
		ActionBar actionbar = getActivity().getActionBar();
		actionbar.setTitle(title);
		mPath = PreferencesReader.getDataDir(getActivity()) + "/Thumbnail/";
		File dirThum = new File(mPath);
		if(!dirThum.exists() || !dirThum.isDirectory()){
			dirThum.mkdirs();
		}
		
		activity = (FragmentActivity) getActivity();
		((zReaderActivity)activity).setOnBackPressedListener(this);
		
		view = inflater.inflate(R.layout.browseitem_layout, container, false);
		myPath = (TextView) view.findViewById(R.id.path);
	
		File storageUri = getActivity().getApplicationContext().getDatabasePath("/storage");
		if(storageUri != null && storageUri.exists()) {
			root = storageUri.getPath();
			
			File f = new File(root);
			File[] files = f.listFiles();
			ArrayList<File> arrFiles = new ArrayList<File>(Arrays.asList(files));
			for (File dir : arrFiles) {
				if(dir.getName().equalsIgnoreCase("emulated"))
					arrFiles.remove(dir);
			}
			if(arrFiles.size() == 1){
				root = arrFiles.get(0).getPath();
			}
			
			String cachePath = PreferencesReader.getCurrentPathBrowse(getActivity());
			if(!cachePath.equals("")) {
				File cPath = new File(cachePath);
				if(cPath.exists() && cPath.isDirectory()){
					getDir(cachePath);
				}else getDir(root);
			}else getDir(root);
			
		}else{
			root = Environment.getExternalStorageDirectory().getPath();
			getDir(root);
		}

		
//		getActivity().registerReceiver(connectivity_receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));		
		
		//Ads  
//		AdRequest.Builder adBuilderButtom = new AdRequest.Builder();
//		adBuilderButtom.addTestDevice("4BD85A785116AD9259A63E9FB48EDFE5");				
//		AdRequest adRequestButtom = adBuilderButtom.build();
//		AdView adView = (AdView) view.findViewById(R.id.adView);
//		adView.loadAd(adRequestButtom);
		//Ads
//		LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//		LocationListener locationListener = new LocationListener() {
//
//			@Override
//			public void onLocationChanged(Location location) {
//				//Ads  
//				AdRequest.Builder adBuilder = new AdRequest.Builder();
//				adBuilder.setLocation(location);
//				adBuilder.addTestDevice("4BD85A785116AD9259A63E9FB48EDFE5");				
//				AdRequest adRequest = adBuilder.build();
//				AdView adView = (AdView) view.findViewById(R.id.adView);
//				adView.loadAd(adRequest);
//				//Ads
//			}
//
//			@Override
//			public void onProviderDisabled(String arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onProviderEnabled(String arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		};
//		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		
		return view;
	}

	private void getDir(final String dirPath) {
		currentPath = dirPath;
		myPath.setText(dirPath);
		path = new ArrayList<String>();
		itemlist = new ArrayList<BrowseItem>();
		File f = new File(dirPath);
		File[] files = f.listFiles();

		for (int i = 0; i < files.length; i++) {
			File file = files[i];

			if (!file.isHidden() && file.canRead()) {
				if (file.isDirectory()) {
					if(!file.getPath().equalsIgnoreCase("/storage/emulated")){
//						path.add(file.getPath());
						// item.add(file.getName() + "/");
						BrowseItem it = new BrowseItem();
						it.setPath(file.getPath());
						it.setName(file.getName());
//						it.setIcon(R.drawable.ic_folder);
						it.setIcon(R.drawable.ic_folder);
						itemlist.add(it);
					}
					
				} else {
					String sName = file.getName().toLowerCase();
					String ext = sName.substring((sName.lastIndexOf(".") + 1), sName.length());
					if (ext.equals("pdf")) {
//						path.add(file.getPath());
						BrowseItem it = new BrowseItem();
						it.setPath(file.getPath());
						it.setName(file.getName());
						int ic = 0;
						if (ext.equals("pdf"))
							ic = R.drawable.ic_action_content_copy;
						it.setIcon(ic);
						itemlist.add(it);
					}
				}
			}
		}

		TextView noDoc = (TextView) view.findViewById(R.id.browseTextNoItem);
		if(itemlist.size() > 0){
			noDoc.setVisibility(View.GONE);
			//sort item
			Collections.sort(itemlist, new Comparator<BrowseItem>() {

				@Override
				public int compare(BrowseItem lhs, BrowseItem rhs) {
					return lhs.getName().compareToIgnoreCase(rhs.getName());
				}
			});
			
			//folder fist
			ArrayList<BrowseItem> folderFirstList = new ArrayList<BrowseItem>();
			ArrayList<BrowseItem> documentList = new ArrayList<BrowseItem>();
			for(BrowseItem item : itemlist) {
				if(item.getIcon() == R.drawable.ic_folder) {
					folderFirstList.add(item);
				}else{
					documentList.add(item);
				}
			}
			itemlist.clear();
			itemlist.addAll(folderFirstList);
			itemlist.addAll(documentList);
			for(BrowseItem item : itemlist) {
				path.add(item.getPath());
			}
			
		}else noDoc.setVisibility(View.VISIBLE);
		
		ListView nList = (ListView) view.findViewById(R.id.item_listview);		
		adapter = new ItemAdapter();
		nList.setAdapter(adapter);
		nList.setOnItemClickListener(this);
		
		
		
		LinearLayout back = (LinearLayout) view.findViewById(R.id.BackBrowseLayout);
		ImageView backButton = (ImageView) view.findViewById(R.id.BackBrowseIcon);
		if(!dirPath.equals(root)){
			back.setVisibility(View.VISIBLE);
			backButton.setVisibility(View.VISIBLE);
			back.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					File fDir = new File(dirPath);
					getDir(fDir.getParent());
				}
			});			
		}else{			
//			back.setVisibility(View.GONE);
			backButton.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		PreferencesReader.saveCurrentPathBrowse(getActivity(), currentPath);
	}

	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		File file = new File(path.get(position));
		if (file.isDirectory()) {
			if (file.canRead()) {
				getDir(path.get(position));
			} else {
				new AlertDialog.Builder(getActivity()).setIcon(R.drawable.ic_launcher)
						.setTitle("[" + file.getName() + "] folder can't be read!").setPositiveButton("OK", null)
						.show();
			}
		} else {
			String sName = file.getName().toLowerCase();
			String ext = sName.substring((sName.lastIndexOf(".") + 1), sName.length());
			if (ext.equals("pdf")) {
				DBRecentBooks db = new DBRecentBooks(getActivity()).open();
				db.addOrUpdateRecentBook(file.getPath(), file.getName(), System.currentTimeMillis());
				db.close();
				
				Uri uri = Uri.fromFile(file);
				Intent intent = new Intent(getActivity(),MuPDFActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(uri);
				startActivity(intent);
			}

		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(adapter != null && adapter.getCount() > 0) {
			adapter.notifyDataSetChanged();
		}
	}
	
//	private BroadcastReceiver connectivity_receiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			NetworkInfo currentNetworkInfo = (NetworkInfo) intent
//					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
//			if (currentNetworkInfo.isConnected()) {
//				try {
//					
//				} catch (Exception e) {
//				}
//			}
//		}
//	};

	private class ItemAdapter extends BaseAdapter {

		public int getCount() {
			return (itemlist == null) ? 0 : itemlist.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			final AQuery aq;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.browseitem, parent, false);
				aq = new AQuery(getActivity(), convertView);
			} else {
				aq = new AQuery(getActivity(), convertView);
			}

			if (itemlist.get(position).getIcon() == R.drawable.ic_folder) {
				aq.id(R.id.item_icon).backgroundColorId(android.R.color.transparent)
						.image(itemlist.get(position).getIcon()).getImageView().setScaleType(ScaleType.FIT_CENTER);				
				aq.id(R.id.FirstTextTitleCover).gone();
				aq.id(R.id.moreoverflowMenu).invisible();
			} else {
				String thumbnail = mPath + PreferencesReader.rePlaceString(itemlist.get(position).getPath()) + "/"
						+ PreferencesReader.rePlaceString(itemlist.get(position).getName() + "_0");
				File imageFile = new File(thumbnail);
				if (imageFile.exists()) {
					aq.id(R.id.item_icon).backgroundColorId(android.R.color.transparent).image(imageFile.getPath())
							.getImageView().setScaleType(ScaleType.CENTER_CROP);
					aq.id(R.id.FirstTextTitleCover).gone();
				} else {
					int colorIndex = position % ZReaderUtils.colorBg.length;
					aq.id(R.id.item_icon).image((Bitmap) null).backgroundColorId(ZReaderUtils.colorBg[colorIndex])
							.getImageView().setScaleType(ScaleType.CENTER_CROP);
					String firstChar = itemlist.get(position).getName().toUpperCase().substring(0, 1);
					aq.id(R.id.FirstTextTitleCover).text(firstChar).visible();
				}
				aq.id(R.id.moreoverflowMenu).visible().clicked(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						showPopup(aq.id(R.id.moreoverflowMenu).getView(), itemlist.get(position).getFile(), position);
					}
				});
			}
			
			aq.id(R.id.item_title).text(itemlist.get(position).getName());

			return convertView;
		}
	}

	@Override
	public boolean doBack() {
		// TODO Auto-generated method stub
		if(!currentPath.equals(root)){
			File fDir = new File(currentPath);
			getDir(fDir.getParent());
			return true;
		}else
			return false;
		
	}
	
	public void showPopup(View v, final File file, final int index) {
		PopupMenu popup = new PopupMenu(getActivity(), v);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.file_menu, popup.getMenu());
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item.getItemId() == R.id.file_properties) {
					showPropertieDialog(getActivity(), file);
					return true;
				} else if (item.getItemId() == R.id.file_rename) {
					showRenameDialog(getActivity(), file, index);
					return true;
				} else if (item.getItemId() == R.id.file_duplicate) {
					duplicateFile(file, index);
					return true;
				} else if (item.getItemId() == R.id.file_move) {
					return true;
				} else if (item.getItemId() == R.id.file_delete) {
					deleteFile(file, index);
					return true;
				} else if (item.getItemId() == R.id.file_share) {
					shareActionSend(file);
					return true;
				}
				return false;
			}
		});
		popup.show();
	}
	
	private AlertDialog reNameDialog, propertieDialog, moveDialog;
	private void showPropertieDialog(Activity act, final File file) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(act);
		LayoutInflater layoutInflater = (LayoutInflater) act.getSystemService(act.LAYOUT_INFLATER_SERVICE);
		View contentView = layoutInflater.inflate(R.layout.properties_dialog, null, false);
		AQuery aq = new AQuery(contentView);
		aq.id(R.id.properite_filename).text(file.getName());
		aq.id(R.id.properite_path).text(file.getParent());
		
		float sMb = ((file.length() / 1024.0f) / 1024.0f);
		NumberFormat nf = NumberFormat.getInstance();
		String sizeBytes = nf.format(file.length());
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		String sizeMb = nf.format(sMb);				
		aq.id(R.id.properite_size).text(sizeMb+" MB ("+sizeBytes+" Bytes)");
		
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa");
		aq.id(R.id.properite_modified).text(sdf.format(file.lastModified()));
		
		String permission;
		if(file.canRead() && file.canWrite()){
			permission = "Read and Write";
		}else if(file.canRead()) {
			permission = "Read only";
		}else if(file.canWrite()) {
			permission = "Write only";
		}else permission = "-";
		aq.id(R.id.properite_permission).text(permission);
		
		aq.id(R.id.properite_buttonCancel).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (propertieDialog != null && propertieDialog.isShowing())
					propertieDialog.cancel();
			}
		});
		
		alertDialog.setView(contentView);
		propertieDialog = alertDialog.create();
		propertieDialog.show();
	}
	
	public void showRenameDialog(final Activity act, final File file,final int index) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(act);
		LayoutInflater layoutInflater = (LayoutInflater) act.getSystemService(act.LAYOUT_INFLATER_SERVICE);
		View contentView = layoutInflater.inflate(R.layout.rename_dialog, null, false);
		final EditText input = (EditText) contentView.findViewById(R.id.editTextBookName);
		input.setText(file.getName());
		Button cancelBtn = (Button) contentView.findViewById(R.id.buttonCancel);
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (reNameDialog != null && reNameDialog.isShowing())
					reNameDialog.cancel();
			}
		});
		Button renameBtn = (Button) contentView.findViewById(R.id.buttonRename);
		renameBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String newName = input.getText().toString();
				if(newName.length() > 0) {
					String lastText = newName.substring(newName.length() - ".pdf".length());
					if(!lastText.equalsIgnoreCase(".pdf")){
						newName = newName+".pdf";
					}
					
					File newFile = new File(file.getParent(), newName);
					if(file.exists() && !newFile.exists()){
						boolean renamed = file.renameTo(newFile);
						
//						core.refreshAdapter();
						if(renamed){
							duplicateThumbnail(act, file, newFile);							
							String sName = newFile.getName().toLowerCase();
							String ext = sName.substring((sName.lastIndexOf(".") + 1), sName.length());
//							if (ext.equals("pdf")) {
								path.set(index, newFile.getPath());
								BrowseItem it = new BrowseItem();
								it.setPath(newFile.getPath());
								it.setName(newFile.getName());
								int ic = 0;
								if (ext.equals("pdf"))
									ic = R.drawable.ic_action_content_copy;
								it.setIcon(ic);
								itemlist.set(index, it);
//							}
							adapter.notifyDataSetChanged();
							DBRecentBooks db = new DBRecentBooks(act).open();
							db.renameOrPath(file, newFile);
							db.close();
						}else {
							Toast.makeText(act, "Cannot rename file "+file.getName(), Toast.LENGTH_SHORT).show();
						}
						
					}					
					if (reNameDialog != null && reNameDialog.isShowing())
						reNameDialog.cancel();
				}
			}
		});
		
		alertDialog.setView(contentView);
		reNameDialog = alertDialog.create();
		reNameDialog.show();
	}
	
	private void duplicateThumbnail(Activity act, File oldFile, File newFile) {
		String mPath = PreferencesReader.getDataDir(act) + "/Thumbnail/";
		File dirThum = new File(mPath);
		if(!dirThum.exists() || !dirThum.isDirectory()){
			dirThum.mkdirs();
		}
		
		String oldThumbnail = mPath + PreferencesReader.rePlaceString(oldFile.getPath()) + "/"
				+ PreferencesReader.rePlaceString(oldFile.getName()+"_0");
		
		String newThumbnail = mPath + PreferencesReader.rePlaceString(newFile.getPath()) + "/"
				+ PreferencesReader.rePlaceString(newFile.getName()+"_0");
		File oldPic = new File(oldThumbnail);
		File newPic = new File(newThumbnail);
		
		if(oldPic.exists()){			
			//create dir
			File dirPic = new File(newPic.getParent());
			if(!dirPic.exists()) dirPic.mkdirs();
			try {
				if(!newPic.exists())
					ZReaderUtils.copy(oldPic, newPic);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private void duplicateFile(File file, final int index){
		String filename = file.getName();
		String sPath = file.getParent();
		String extension = FilenameUtils.getExtension(filename);
		String fileNameWithOutExt = FilenameUtils.removeExtension(filename);
		
		String newFileName = null;
		int num = 1;
		File newFile = null;
		boolean added = false;
		if(fileNameWithOutExt.substring(fileNameWithOutExt.length()-1, fileNameWithOutExt.length()).equals(")") && 
				fileNameWithOutExt.substring(fileNameWithOutExt.length()-3, fileNameWithOutExt.length()-2).equals("(")) {
			String numstr = fileNameWithOutExt.substring(fileNameWithOutExt.length()-2, fileNameWithOutExt.length()-1);
			if(ZReaderUtils.isIntNum(numstr)){
				num = Integer.parseInt(numstr);
				++num;
				added = true;
			}else added = false;
		} else {
			added = false;
		}
		
		if(added) {
			newFileName = fileNameWithOutExt.substring(0, fileNameWithOutExt.length()-3)+"("+num+")";
		}else {
			newFileName = fileNameWithOutExt+"("+num+")";
		}
		
		String newPath = sPath+"/"+newFileName+"."+extension;
		newFile = new File(newPath);
		while (newFile.exists()) {
			++num;
			if(added) {
				newFileName = fileNameWithOutExt.substring(0, fileNameWithOutExt.length()-3)+"("+num+")";
			}else {
				newFileName = fileNameWithOutExt+"("+num+")";
			}
			newPath = sPath+"/"+newFileName+"."+extension;
			newFile = new File(newPath);
		}

		new AsyncTask<File, Void, Boolean>() {
			
			private File sNewFile, sFile;
//			ProgressDialog progressDialog;
			
			@Override
			protected void onPreExecute() {
//				progressDialog= ProgressDialog.show(activity.getApplicationContext(), null,"Loading...", true);
				activity.setProgressBarIndeterminateVisibility(true);
			};

			@Override
			protected Boolean doInBackground(File... params) {
				sFile = params[0];
				sNewFile = params[1];
				try {
					ZReaderUtils.copy(sFile, sNewFile);
					duplicateThumbnail(getActivity(), sFile, sNewFile);

					String sName = sNewFile.getName().toLowerCase();
					String ext = sName.substring((sName.lastIndexOf(".") + 1), sName.length());
					path.add(index+1, sNewFile.getPath());
					BrowseItem it = new BrowseItem();
					it.setPath(sNewFile.getPath());
					it.setName(sNewFile.getName());
					int ic = 0;
					if (ext.equals("pdf"))
						ic = R.drawable.ic_action_content_copy;
					it.setIcon(ic);
					itemlist.add(index+1, it);

					DBRecentBooks db = new DBRecentBooks(getActivity()).open();
					db.addOrUpdateRecentBook(sNewFile.getPath(), sNewFile.getName(), System.currentTimeMillis());
					db.close();	
					return true;
				} catch (IOException e) {
					e.printStackTrace();
					return false;					
				}
			}
			
			protected void onPostExecute(Boolean result) {
//				progressDialog.dismiss();
				activity.setProgressBarIndeterminateVisibility(false);
				if(!result){
					Toast.makeText(getActivity(), "Cannot duplicate file "+sFile.getName(), Toast.LENGTH_SHORT).show();
				}else {
					adapter.notifyDataSetChanged();
				}
			};
			
		}.execute(file, newFile);
	}
	
	private void deleteFile(File file, int index) {
		boolean deleted = file.delete();
		if(deleted){						
			//remove thumbnail
			String thumbnail = mPath + PreferencesReader.rePlaceString(itemlist.get(index).getPath()) + "/"
					+ PreferencesReader.rePlaceString(itemlist.get(index).getName()+"_0");
			File imageFile = new File(thumbnail);
			imageFile.delete();
			
			itemlist.remove(index);
			path.remove(index);
			adapter.notifyDataSetChanged();	
		}else {
			Toast.makeText(getActivity(), "Cannot delete file "+file.getName(), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void shareActionSend(File file) {
		Intent intent = new Intent(android.content.Intent.ACTION_SEND); 
        intent.setType("application/pdf");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, file.getName().replace(".pdf", "").replace("_", " ").replace("-", " "));		             
        intent.putExtra(android.content.Intent.EXTRA_TEXT,  "");     
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(Intent.createChooser(intent, "Share PDF"));
	}

}
