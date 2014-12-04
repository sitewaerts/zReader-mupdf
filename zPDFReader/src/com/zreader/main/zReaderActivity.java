package com.zreader.main;

import java.util.List;
import java.util.Locale;
import java.util.Stack;

import com.androidquery.AQuery;
//import com.google.analytics.tracking.android.EasyTracker;
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.InterstitialAd;
//import com.zreader.inappbilling.util.IabHelper;
//import com.zreader.inappbilling.util.IabHelper.OnConsumeFinishedListener;
//import com.zreader.inappbilling.util.IabHelper.OnIabSetupFinishedListener;
//import com.zreader.inappbilling.util.IabResult;
//import com.zreader.inappbilling.util.Inventory;
//import com.zreader.inappbilling.util.Purchase;
//import com.zreader.inappbilling.util.SkuDetails;
import com.zreader.utils.PreferencesReader;


import android.app.Activity;
import android.app.ActionBar;
//import android.app.Fragment;
//import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class zReaderActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	// ViewPager mViewPager;
	// PagerSlidingTabStrip tabsStrip;
	private String[] mPlanetTitles;
	private static int currentPage;

	// private View mainView;
	private LinearLayout mainLayout;
	private DrawerLayout mDrawerLayout;
	private static ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	protected OnBackPressedListener onBackPressedListener;	
//	private InterstitialAd intAd;
//	
//	private AdView adView;
	private boolean hideAds;
	private Context context;
	private String tag = "in_app_billing_remove_ads";
//	private IabHelper mHelper;
	private final String base64PublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxV+WRZWv1Iie+DK9TGud65CU8ZRb3gQAD5o8fhgfxXXphViI2TJeTHyo8kQB2GVq6+foMG+GWnnwxlng+noASSNGn+j/zWZWq12HMKKDh95aPfqrOpPW65hPoE05MYfuKyzvA4oNlyIows/K4ViIwLpia2ZRuYxVfiEzM9JucnZ8j+07O4LERT5Z7grkVTotpmBhKEEJM+uDWvUnvwq1Wj5aVWDOshYdEBhxZGxvCaNYdGMJtkQZfN2y3IkJgUP5MGENGJYAtHvPpvgaYUcGbftja0V7KrBHT8uSGlXvs9WcYKbgUGncdDsIBU+GBE6MqchAQmCeiKhyAyQ96uhUZQIDAQAB";
	private boolean isSetup;
	private final String productID = "zreader.remove.ads.first";//"zreader.remove.ads";
//	private Purchase purchaseOwned;
	private String dataPayLoad = "lioehidevzreaderburapha";
	
//	private String removeAdsPayload = "qwertyuiinappads";
	
	@Override
	protected void onStart() {
		super.onStart();
//		EasyTracker.getInstance(this).activityStart(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
//		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);		
		setContentView(R.layout.main_drawerlayout);
		
		context = getApplicationContext();

//		adView = (AdView) findViewById(R.id.adView);
		mainLayout = (LinearLayout) findViewById(R.id.content_frame_layout);
		mPlanetTitles = getResources().getStringArray(R.array.main_category_list);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		DrawerListItem adapter = new DrawerListItem(mPlanetTitles);
		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position, false);
			}
		});

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_navigation_drawer,
				R.string.app_name, R.string.app_name) {
			@Override
			public void onDrawerClosed(View drawerView) {
				// supportInvalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				// supportInvalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(R.drawable.ic_launcher2);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		currentPage = 0;
		if(savedInstanceState != null) {
			currentPage = savedInstanceState.getInt("CurrentFragment");
			selectItem(currentPage, false);
		}else{
			if(PreferencesReader.isFirstTime(this)) 
				currentPage = 1;
			
			setTitle(mPlanetTitles[currentPage]);
			Bundle args = new Bundle();
			args.putInt("section_number", currentPage);
			args.putString("title", mPlanetTitles[currentPage]);
			mDrawerList.setItemChecked(currentPage, true);
			Fragment fragment = Fragment.instantiate(this, currentPage == 0 ? RecentlyFragment.class.getName()
					: ShowAllEBooksFragment.class.getName(), args);
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
		}
		
		setProgressBarIndeterminateVisibility(false);
		

		//In app setup
		hideAds = PreferencesReader.getRemoveAdsPurchased(this);
		if(!hideAds){
//			ShowAdsView();
		}
				
//		mHelper = new IabHelper(context, base64PublicKey);	
//		try {
//			mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
//
//				@Override
//				public void onIabSetupFinished(IabResult result) {
//					isSetup = result.isSuccess();
//					if(isSetup){
//						checkPurchased();
//					}
//				}
//			});
//		} catch (Exception e) {
//			e.printStackTrace();
//			isSetup = false;
//		}
	}
	
//	private void ShowAdsView() {
//		// Show Ads
//		AdRequest.Builder adBuilder = new AdRequest.Builder();
//		adBuilder.addTestDevice("4BD85A785116AD9259A63E9FB48EDFE5");
//		AdRequest adRequest = adBuilder.build();
//		intAd = new InterstitialAd(this);
//		intAd.setAdUnitId("ca-app-pub-4879286782597366/4138255537");
//		intAd.loadAd(adRequest);
//		intAd.setAdListener(new AdListener() {
//			public void onAdClosed() {
//				finish();
//			}
//		});
//
//		AdRequest.Builder adBuilderButtom = new AdRequest.Builder();
//		adBuilderButtom.addTestDevice("4BD85A785116AD9259A63E9FB48EDFE5");
//		AdRequest adRequestButtom = adBuilderButtom.build();
//		
//		adView.setAdListener(new AdListener() {
//			@Override
//			public void onAdLoaded() {
//				if(!hideAds){
//					adView.setVisibility(View.VISIBLE);
//					showRemoveAdsButton(true);
//				}				
//				super.onAdLoaded();
//			}
//		});
//		adView.loadAd(adRequestButtom);
//	}
//	
	View footerView = null;
	private void showRemoveAdsButton(boolean show){
		if(show) {	
			if(mDrawerList.getFooterViewsCount() == 0){
				LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				footerView = li.inflate(R.layout.remove_ads_item, null);
				LinearLayout removeLay = (LinearLayout) footerView.findViewById(R.id.removeAdsLayout);
//				Button removeBtn = (Button) footerView.findViewById(R.id.removeAds);
//				removeBtn.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View arg0) {
//						mHelper.consumeAsync(purchaseOwned, new OnConsumeFinishedListener() {
//							
//							@Override
//							public void onConsumeFinished(Purchase purchase, IabResult result) {
//								boolean blnSuccess = result.isSuccess();
//								boolean blnFail = result.isFailure();
//								Log.i(tag, "mHelper.consumeAsync() - blnSuccess return " + String.valueOf(blnSuccess));
//								Log.i(tag, "mHelper.consumeAsync() - blnFail return " + String.valueOf(blnFail));
//
//							}
//						});
//					}
//				});
//				removeLay.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View arg0) {
//						launchPurchase();
//					}
//				});
				mDrawerList.addFooterView(footerView);
			}			
		}else {
			if(mDrawerList.getFooterViewsCount() > 0) {
				if(footerView != null) {
					mDrawerList.removeFooterView(footerView);
					footerView = null;
				}
			}
		}
	}
	
//	private void checkPurchased(){
//		mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
//			
//			@Override
//			public void onQueryInventoryFinished(IabResult result, Inventory inv) {
//				boolean blnSuccess = result.isSuccess();
//				boolean blnFail = result.isFailure();
//				Log.i(tag, "mHelper.onQueryInventoryFinished() - blnSuccess return " + String.valueOf(blnSuccess));
//				Log.i(tag, "mHelper.onQueryInventoryFinished() - blnFail return " + String.valueOf(blnFail));
//				if (!blnSuccess)
//					return;
//
//				if (!inv.hasPurchase(productID)) {
//					PreferencesReader.saveRemoveAdsPurchased(zReaderActivity.this, false);
//					if (hideAds) {
//						hideAds = false;
////						ShowAdsView();
//					}
//				} else {
//					purchaseOwned = inv.getPurchase(productID);
////					System.out.println("Debug queryInventoryAsync getPurchase : "+purchaseOwned.getDeveloperPayload());
//					PreferencesReader.saveRemoveAdsPurchased(zReaderActivity.this, true);
//					if (!hideAds) {
//						hideAds = true;
////						adView.setVisibility(View.GONE);
//						showRemoveAdsButton(false);
//					}
//					SkuDetails skuDetails = inv.getSkuDetails(productID);
//				}
//
//			}
//		});
//	}
	
//	private void launchPurchase(){
//		mHelper.launchPurchaseFlow(this, productID, 1001, new IabHelper.OnIabPurchaseFinishedListener() {
//			@Override
//			public void onIabPurchaseFinished(IabResult result, Purchase info) {
//				boolean blnSuccess = result.isSuccess();
//				boolean blnFail = result.isFailure();
//
//				Log.i(tag, "mHelper.launchPurchaseFlow() - blnSuccess return " + String.valueOf(blnSuccess));
//				Log.i(tag, "mHelper.launchPurchaseFlow() - blnFail return " + String.valueOf(blnFail));
//
//				if (!blnSuccess) return;
//
//				purchaseOwned = info;
//				String payload = purchaseOwned.getDeveloperPayload();
////				System.out.println("Debug Purchase Payload : "+payload);
//				
//				PreferencesReader.saveRemoveAdsPurchased(zReaderActivity.this, true);	
//				hideAds = true;
////				adView.setVisibility(View.GONE);
//				showRemoveAdsButton(false);
//				
//			}
//		}, dataPayLoad);
//	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (isSetup) {
//			boolean blnResult = mHelper.handleActivityResult(requestCode, resultCode, data);
//			System.out.println("Debug onActivityResult "+blnResult);
//			if (blnResult){				
				
//				checkPurchased();
				
//				PreferencesReader.saveRemoveAdsPurchased(zReaderActivity.this, true);				
//				hideAds = true;
//				adView.setVisibility(View.GONE);
//				showRemoveAdsButton(false);
//				return;
//			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
//		if (isSetup) mHelper.dispose();
//		mHelper = null;
		super.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("CurrentFragment", currentPage);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.core_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		int id = item.getItemId();
		if (id == R.id.core_menu_search) {
			startActivity(new Intent(this, SearchFileActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 5;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			case 4:
				return getString(R.string.title_section5).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		private String title;
		private int index;

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Bundle args = getArguments();
			title = args.getString("title");
			index = args.getInt("section_number");
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			getActivity().getActionBar().setTitle(title);
			zReaderActivity.setItemChecked(index);

			View rootView = inflater.inflate(R.layout.fragment_current, container, false);
			TextView textView = (TextView) rootView.findViewById(R.id.section_label);
			textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	private void selectItem(int position, boolean isRefresh) {
		if (currentPage != position || isRefresh) {
			Fragment fragment = null;
			Bundle args = new Bundle();
			FragmentManager fragmentManager = getSupportFragmentManager();
			switch (position) {
			case 0:
				args.putInt("section_number", position);
				args.putString("title", mPlanetTitles[position]);

				fragment = Fragment.instantiate(this, RecentlyFragment.class.getName(), args);
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment)/*.addToBackStack("fragBack")*/
						.commit();

				break;
			case 1:
				args.putInt("section_number", position);
				args.putString("title", mPlanetTitles[position]);
				fragment = Fragment.instantiate(this, ShowAllEBooksFragment.class.getName(), args);
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment)/*.addToBackStack("fragBack")*/
						.commit();
				break;
			case 2:
				args.putInt("section_number", position);
				args.putString("title", mPlanetTitles[position]);
				fragment = Fragment.instantiate(this, BrowseFileFragment.class.getName(), args);
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment)/*.addToBackStack("fragBack")*/
						.commit();
				break;

			default:
				break;
			}

			// setTitle(mPlanetTitles[position]);
			currentPage = position;
			mDrawerList.setItemChecked(currentPage, true);
		}
		mDrawerLayout.closeDrawers();
	}

	public static void setItemChecked(int position) {
		currentPage = position;
		mDrawerList.setItemChecked(position, true);
	}

	class DrawerListItem extends BaseAdapter {

		private String[] items;
		private int[] iconId = new int[] { R.drawable.ic_action_device_access_time,
				R.drawable.ic_action_content_copy, R.drawable.ic_action_collections_collection };

		public DrawerListItem(String[] items) {
			this.items = items;
		}

		@Override
		public int getCount() {
			return items.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final AQuery aq;
			if (convertView == null) {
				convertView = LayoutInflater.from(zReaderActivity.this.getApplicationContext()).inflate(
						R.layout.drawer_list_item, parent, false);
			}
			aq = new AQuery(convertView);
			aq.id(R.id.DrawerListIcon).image(iconId[position]);
			aq.id(R.id.DrawerListTitle).text(items[position]);
			return convertView;
		}

	}
	
	public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
	    this.onBackPressedListener = onBackPressedListener;
	}
	
	private static long back_pressed;
	
	@Override
	public void onBackPressed() {
		if (onBackPressedListener != null){
			if(onBackPressedListener.doBack()){
				return;
			}
		}
		
		List<Fragment> frag = getSupportFragmentManager().getFragments();
		if (frag.get(frag.size()-1) instanceof RecentlyFragment) {
			
			if (back_pressed + 2000 > System.currentTimeMillis()){
//				if(!hideAds && intAd !=null && intAd.isLoaded()) {
//					intAd.show();
//				}else //finish();
//					super.onBackPressed();
		    } else{
		        Toast.makeText(getBaseContext(), getResources().getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
		        back_pressed = System.currentTimeMillis();
		    }
						
			
			
		} else {
			Fragment fragment = null;
			Bundle args = new Bundle();
			FragmentManager fragmentManager = getSupportFragmentManager();
			args.putInt("section_number", 0);
			args.putString("title", mPlanetTitles[0]);

			fragment = Fragment.instantiate(this, RecentlyFragment.class.getName(), args);
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
			
			currentPage = 0;
			mDrawerList.setItemChecked(currentPage, true);
		}
		
		
	}
}
