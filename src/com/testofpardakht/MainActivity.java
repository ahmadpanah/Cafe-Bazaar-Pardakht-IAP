package com.testofpardakht;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.testofpardakht.R;
import com.testofpardakht.util.IabHelper;
import com.testofpardakht.util.IabResult;
import com.testofpardakht.util.Inventory;
import com.testofpardakht.util.Purchase;

public class MainActivity extends Activity{
	IabHelper mHelper;
	static final int RC_REQUEST = 10001;
	boolean mIsPremium = false;
	
		// بسیار مهم : این دو مقدار را حتما عوض کنید ! // ×××
			String product_id = "donate";
				String base64EncodedPublicKey = "****************";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_view);

		loadHelper();
	}

	private void loadHelper() 
	{
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() 
		{
			public void onIabSetupFinished(IabResult result) 
			{
				if (!result.isSuccess()) 
				{
					Log.d("TAG", "...");
					return;
				}

				if (mHelper == null) return;
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		Log.d("TAG", "...");
		if (mHelper == null) return;

		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) 
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
		else 
		{
			Log.d("TAG", "...");
		}
	}

	public void loading(View v) 
	{
		String payload = "";
		mHelper.launchPurchaseFlow(this, product_id, IabHelper.ITEM_TYPE_SUBS, RC_REQUEST, mPurchaseFinishedListener, payload);
	}

	// قسمت برگشت به برنامه بعد از پرداخت
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() 
	{
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) 
		{
			if (mHelper == null) return;

			if (result.isFailure()) 
			{
				Log.d("TAG", "...");
				return;
			}
			if (!verifyDeveloperPayload(purchase)) 
			{
				Log.d("TAG", "...");
				return;
			}

			if (purchase.getSku().equals(product_id)) 
			{
				mHelper.consumeAsync(purchase, mConsumeFinishedListener);
			}
		}
	};

	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();

		return true;
	}

	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() 
	{
		public void onConsumeFinished(Purchase purchase, IabResult result) 
		{
			
			// پیام موفق بودن خرید
				alert("ممنون از حمایت شما");
					Log.d("TAG", "تمام");
			
		}
	};

	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() 
	{
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) 
		{

			if (mHelper == null) return;

			if (result.isFailure()) {
				return;
			}

			Purchase premiumPurchase = inventory.getPurchase(product_id);
			mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
			mIsPremium = false;
		}
	};

	private void alert(String string) 
	{
		Toast.makeText(MainActivity.this, string, Toast.LENGTH_LONG).show();
	}
	
}